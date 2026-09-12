package com.example.ai

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Unified AI Service orchestrator supporting multiple modular providers:
 * - GeminiProvider (Reasoning, STEM, Mathematics, Coding, Vision, Multimodal)
 * - ImageProvider (Gemini Generative Image Model)
 * - FutureVideoProvider (Staged for Google Veo)
 * - VoiceService (Microphone dictation and Text-to-Speech)
 */
class AIService(context: Context) {
    val geminiProvider = GeminiProvider()
    val imageProvider = ImageProvider()
    val futureVideoProvider = FutureVideoProvider()
    val voiceService = VoiceService(context)

    fun streamChat(
        history: List<AIChatMessage>,
        model: GeminiModel = GeminiModel.GEMINI_3_5_FLASH,
        educationMode: EducationMode = EducationMode.DEFAULT,
        temperature: Float = 0.7f
    ): Flow<String> {
        return geminiProvider.streamChat(history, model, educationMode, temperature)
    }

    suspend fun generateContent(
        history: List<AIChatMessage>,
        model: GeminiModel = GeminiModel.GEMINI_3_5_FLASH,
        educationMode: EducationMode = EducationMode.DEFAULT,
        temperature: Float = 0.7f
    ): AIResult<String> {
        return geminiProvider.generateResponse(history, model, educationMode, temperature)
    }

    suspend fun generateImage(
        prompt: String,
        aspectRatio: String = "1:1"
    ): AIResult<GeneratedImageResult> {
        return imageProvider.generateImage(prompt, aspectRatio)
    }
}
