package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIChatMessage
import com.example.ai.AIService
import com.example.ai.AIResult
import com.example.ai.EducationMode
import com.example.ai.GeminiModel
import com.example.ai.GeneratedImageResult
import com.example.data.ChatMessageEntity
import com.example.data.ConversationEntity
import com.example.data.KigoDatabase
import com.example.data.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

enum class AppDestination {
    CHAT,
    IMAGE_GENERATION,
    SETTINGS,
    ABOUT
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = KigoDatabase.getInstance(application)
    private val repository = ChatRepository(database.chatDao())
    val aiService = AIService(application)

    // Destination
    private val _currentDestination = MutableStateFlow(AppDestination.CHAT)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Conversations
    val conversations: StateFlow<List<ConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Conversation
    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    // Current Messages
    val messages: StateFlow<List<ChatMessageEntity>> = _currentConversationId
        .flatMapLatest { convId ->
            if (convId != null) {
                repository.getMessagesForConversation(convId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI & Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _streamingResponseText = MutableStateFlow("")
    val streamingResponseText: StateFlow<String> = _streamingResponseText.asStateFlow()

    private var activeGenerationJob: Job? = null

    // Attached image state for Vision
    private val _selectedImageBase64 = MutableStateFlow<String?>(null)
    val selectedImageBase64: StateFlow<String?> = _selectedImageBase64.asStateFlow()

    private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

    // Image Generation Studio State
    private val _imageGenPrompt = MutableStateFlow("")
    val imageGenPrompt: StateFlow<String> = _imageGenPrompt.asStateFlow()

    private val _imageGenAspectRatio = MutableStateFlow("1:1")
    val imageGenAspectRatio: StateFlow<String> = _imageGenAspectRatio.asStateFlow()

    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _imageGenError = MutableStateFlow<String?>(null)
    val imageGenError: StateFlow<String?> = _imageGenError.asStateFlow()

    private val _generatedImagesList = MutableStateFlow<List<GeneratedImageResult>>(emptyList())
    val generatedImagesList: StateFlow<List<GeneratedImageResult>> = _generatedImagesList.asStateFlow()

    // Settings
    private val _selectedModel = MutableStateFlow(GeminiModel.GEMINI_3_5_FLASH)
    val selectedModel: StateFlow<GeminiModel> = _selectedModel.asStateFlow()

    private val _educationMode = MutableStateFlow(EducationMode.DEFAULT)
    val educationMode: StateFlow<EducationMode> = _educationMode.asStateFlow()

    private val _temperature = MutableStateFlow(0.7f)
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _autoSpeak = MutableStateFlow(false)
    val autoSpeak: StateFlow<Boolean> = _autoSpeak.asStateFlow()

    // Voice
    val isVoiceListening: StateFlow<Boolean> = aiService.voiceService.isListening
    val isVoiceSpeaking: StateFlow<Boolean> = aiService.voiceService.isSpeaking

    private val _voiceStatusMessage = MutableStateFlow<String?>(null)
    val voiceStatusMessage: StateFlow<String?> = _voiceStatusMessage.asStateFlow()

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun startNewChat() {
        stopGeneration()
        _currentConversationId.value = null
        _streamingResponseText.value = ""
        clearSelectedImage()
        _currentDestination.value = AppDestination.CHAT
    }

    fun selectConversation(id: String) {
        stopGeneration()
        _currentConversationId.value = id
        _streamingResponseText.value = ""
        clearSelectedImage()
        _currentDestination.value = AppDestination.CHAT
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            if (_currentConversationId.value == id) {
                _currentConversationId.value = null
            }
            repository.deleteConversation(id)
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            _currentConversationId.value = null
            repository.deleteAll()
        }
    }

    fun setSelectedImage(bitmap: Bitmap) {
        _selectedImageBitmap.value = bitmap
        _selectedImageBase64.value = bitmap.toBase64()
    }

    fun clearSelectedImage() {
        _selectedImageBitmap.value = null
        _selectedImageBase64.value = null
    }

    fun updateModel(model: GeminiModel) {
        _selectedModel.value = model
    }

    fun updateEducationMode(mode: EducationMode) {
        _educationMode.value = mode
    }

    fun updateTemperature(temp: Float) {
        _temperature.value = temp
    }

    fun toggleAutoSpeak() {
        _autoSpeak.value = !_autoSpeak.value
    }

    fun setImageGenPrompt(prompt: String) {
        _imageGenPrompt.value = prompt
    }

    fun setImageGenAspectRatio(ratio: String) {
        _imageGenAspectRatio.value = ratio
    }

    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        val imageBase64 = _selectedImageBase64.value
        if (trimmed.isBlank() && imageBase64 == null) return

        val userMessageContent = if (trimmed.isBlank() && imageBase64 != null) {
            "Analyze and explain this image."
        } else {
            trimmed
        }

        val attachedBase64 = imageBase64
        clearSelectedImage()

        viewModelScope.launch {
            // Ensure we have an active conversation
            var convId = _currentConversationId.value
            if (convId == null) {
                val title = generateTitleFromPrompt(userMessageContent)
                val newConv = ConversationEntity(
                    id = UUID.randomUUID().toString(),
                    title = title
                )
                repository.saveConversation(newConv)
                convId = newConv.id
                _currentConversationId.value = convId
            }

            // Save user message
            val userMsg = ChatMessageEntity(
                conversationId = convId,
                role = "user",
                content = userMessageContent,
                imageBase64 = attachedBase64
            )
            repository.saveMessage(userMsg)

            // Stream response
            executeChatGeneration(convId, userMsg)
        }
    }

    fun regenerateLastResponse() {
        val convId = _currentConversationId.value ?: return
        val currentMsgs = messages.value
        if (currentMsgs.isEmpty()) return

        // Find last user message
        val lastUserMsg = currentMsgs.lastOrNull { it.role == "user" } ?: return
        executeChatGeneration(convId, lastUserMsg)
    }

    private fun executeChatGeneration(convId: String, latestUserMsg: ChatMessageEntity) {
        activeGenerationJob?.cancel()
        _isGenerating.value = true
        _streamingResponseText.value = ""

        activeGenerationJob = viewModelScope.launch {
            val allMsgs = messages.value
            val history = mutableListOf<AIChatMessage>()

            for (m in allMsgs) {
                history.add(
                    AIChatMessage(
                        role = m.role,
                        text = m.content,
                        imageBase64 = m.imageBase64
                    )
                )
            }

            // If latestUserMsg wasn't in messages yet, append it
            if (history.isEmpty() || history.last().text != latestUserMsg.content) {
                history.add(
                    AIChatMessage(
                        role = "user",
                        text = latestUserMsg.content,
                        imageBase64 = latestUserMsg.imageBase64
                    )
                )
            }

            val accumulated = StringBuilder()

            try {
                aiService.streamChat(
                    history = history,
                    model = _selectedModel.value,
                    educationMode = _educationMode.value,
                    temperature = _temperature.value
                ).collect { chunk ->
                    accumulated.append(chunk)
                    _streamingResponseText.value = accumulated.toString()
                }

                val finalResponse = accumulated.toString()
                if (finalResponse.isNotBlank()) {
                    val aiMsg = ChatMessageEntity(
                        conversationId = convId,
                        role = "model",
                        content = finalResponse
                    )
                    repository.saveMessage(aiMsg)

                    if (_autoSpeak.value) {
                        aiService.voiceService.speak(finalResponse)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = if (accumulated.isNotEmpty()) {
                    accumulated.toString()
                } else {
                    "KIGO AI couldn't connect to the AI service. Please check your network or API key."
                }
                val aiErrorMsg = ChatMessageEntity(
                    conversationId = convId,
                    role = "model",
                    content = errorMsg,
                    isError = true
                )
                repository.saveMessage(aiErrorMsg)
            } finally {
                _isGenerating.value = false
                _streamingResponseText.value = ""
            }
        }
    }

    fun stopGeneration() {
        activeGenerationJob?.cancel()
        val partial = _streamingResponseText.value
        val convId = _currentConversationId.value
        if (partial.isNotBlank() && convId != null) {
            viewModelScope.launch {
                val partialMsg = ChatMessageEntity(
                    conversationId = convId,
                    role = "model",
                    content = "$partial\n\n*(Generation stopped)*"
                )
                repository.saveMessage(partialMsg)
                _streamingResponseText.value = ""
                _isGenerating.value = false
            }
        } else {
            _streamingResponseText.value = ""
            _isGenerating.value = false
        }
    }

    fun triggerImageGeneration(prompt: String, ratio: String = "1:1") {
        val trimmed = prompt.trim()
        if (trimmed.isBlank()) return

        _isGeneratingImage.value = true
        _imageGenError.value = null

        viewModelScope.launch {
            when (val result = aiService.generateImage(trimmed, ratio)) {
                is AIResult.Success -> {
                    val list = _generatedImagesList.value.toMutableList()
                    list.add(0, result.data)
                    _generatedImagesList.value = list
                    _isGeneratingImage.value = false

                    // Also record in current chat if chat is active
                    val convId = _currentConversationId.value
                    if (convId != null) {
                        val aiMsg = ChatMessageEntity(
                            conversationId = convId,
                            role = "model",
                            content = result.data.caption ?: "Generated image for: \"$trimmed\"",
                            generatedImageBase64 = result.data.imageBase64
                        )
                        repository.saveMessage(aiMsg)
                    }
                }
                is AIResult.Error -> {
                    _imageGenError.value = result.message
                    _isGeneratingImage.value = false
                }
            }
        }
    }

    fun startVoiceDictation(onRecognized: (String) -> Unit) {
        _voiceStatusMessage.value = "Listening..."
        aiService.voiceService.startListening(
            onResult = { text ->
                _voiceStatusMessage.value = null
                onRecognized(text)
            },
            onError = { error ->
                _voiceStatusMessage.value = error
            }
        )
    }

    fun stopVoiceDictation() {
        aiService.voiceService.stopListening()
        _voiceStatusMessage.value = null
    }

    fun speak(text: String) {
        aiService.voiceService.speak(text)
    }

    fun stopSpeaking() {
        aiService.voiceService.stopSpeaking()
    }

    private fun generateTitleFromPrompt(prompt: String): String {
        val cleaned = prompt.take(35).replace("\n", " ").trim()
        return if (cleaned.length < prompt.length) "$cleaned..." else cleaned
    }

    override fun onCleared() {
        super.onCleared()
        aiService.voiceService.release()
    }
}

fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    val maxDim = 1024
    val scaledBitmap = if (width > maxDim || height > maxDim) {
        val ratio = width.toFloat() / height.toFloat()
        val newWidth = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
        val newHeight = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
        Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
    } else {
        this
    }
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

fun base64ToBitmap(base64: String): Bitmap? {
    return try {
        val decoded = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    } catch (e: Exception) {
        null
    }
}
