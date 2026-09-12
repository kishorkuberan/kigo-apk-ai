package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class GeminiProvider {
    companion object {
        private const val TAG = "GeminiProvider"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
        
        const val BASE_SYSTEM_INSTRUCTION =
            "You are KIGO AI, a helpful, accurate and professional AI assistant. " +
            "Understand the user's intent before answering. Provide clear and useful answers. " +
            "For mathematical and technical problems, reason carefully and verify calculations when possible. " +
            "Show clear calculation steps and the final answer clearly. " +
            "Adapt explanations to the user's requested level. Do not invent facts. " +
            "If information is uncertain, clearly state the uncertainty. " +
            "For coding questions, provide properly formatted code blocks with language tags, explain key parts, and offer fixes for bugs. " +
            "Follow legitimate user instructions. Refuse unsafe requests appropriately and provide safe alternatives where possible."
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val isKeyConfigured: Boolean
        get() {
            val key = BuildConfig.GEMINI_API_KEY
            return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        }

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    /**
     * Stream response chunks from Gemini for real-time responsiveness
     */
    fun streamChat(
        history: List<AIChatMessage>,
        model: GeminiModel = GeminiModel.GEMINI_3_5_FLASH,
        educationMode: EducationMode = EducationMode.DEFAULT,
        temperature: Float = 0.7f
    ): Flow<String> = callbackFlow {
        val apiKey = getApiKey()
        if (!isKeyConfigured) {
            trySend("KIGO AI Error: GEMINI_API_KEY is not configured. Please set your API key in the AI Studio Secrets panel.")
            close()
            return@callbackFlow
        }

        val requestBodyJson = buildRequestBody(history, educationMode, temperature)
        val url = "$BASE_URL${model.modelId}:streamGenerateContent?key=$apiKey&alt=sse"

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val call = client.newCall(request)

        try {
            val response = withContext(Dispatchers.IO) { call.execute() }
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                Log.e(TAG, "API Error: ${response.code} $errorBody")
                val userFriendlyMessage = formatErrorMessage(response.code, errorBody)
                trySend(userFriendlyMessage)
                close()
                return@callbackFlow
            }

            val inputStream = response.body?.byteStream()
            if (inputStream == null) {
                trySend("KIGO AI couldn't read the response from the AI service. Please try again.")
                close()
                return@callbackFlow
            }

            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            withContext(Dispatchers.IO) {
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: continue
                    if (currentLine.startsWith("data: ")) {
                        val jsonString = currentLine.removePrefix("data: ").trim()
                        if (jsonString.isNotEmpty()) {
                            try {
                                val json = JSONObject(jsonString)
                                val textChunk = extractTextFromResponse(json)
                                if (textChunk.isNotEmpty()) {
                                    trySend(textChunk)
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "SSE parse error: ${e.message}")
                            }
                        }
                    }
                }
            }
            close()
        } catch (e: Exception) {
            Log.e(TAG, "Network or stream exception", e)
            trySend("KIGO AI couldn't connect to the AI service. Please check your internet connection and try again.")
            close(e)
        }

        awaitClose {
            call.cancel()
        }
    }

    /**
     * Non-streaming generate content call
     */
    suspend fun generateResponse(
        history: List<AIChatMessage>,
        model: GeminiModel = GeminiModel.GEMINI_3_5_FLASH,
        educationMode: EducationMode = EducationMode.DEFAULT,
        temperature: Float = 0.7f
    ): AIResult<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!isKeyConfigured) {
            return@withContext AIResult.Error(
                "GEMINI_API_KEY is not configured. Please set your API key in the AI Studio Secrets panel."
            )
        }

        try {
            val requestBodyJson = buildRequestBody(history, educationMode, temperature)
            val url = "$BASE_URL${model.modelId}:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API Error: ${response.code} $responseBody")
                return@withContext AIResult.Error(formatErrorMessage(response.code, responseBody))
            }

            val json = JSONObject(responseBody)
            val text = extractTextFromResponse(json)
            if (text.isNotBlank()) {
                AIResult.Success(text)
            } else {
                AIResult.Error("KIGO AI received an empty response. Please try again.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during generateResponse", e)
            AIResult.Error("KIGO AI couldn't connect to the AI service. Please try again.", e)
        }
    }

    private fun buildRequestBody(
        history: List<AIChatMessage>,
        educationMode: EducationMode,
        temperature: Float
    ): JSONObject {
        val root = JSONObject()

        // System Instruction
        var fullInstruction = BASE_SYSTEM_INSTRUCTION
        if (educationMode.instruction.isNotBlank()) {
            fullInstruction += " Education Mode Active: " + educationMode.instruction
        }

        val systemInstructionObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", fullInstruction))
        systemInstructionObj.put("parts", sysParts)
        root.put("systemInstruction", systemInstructionObj)

        // Contents (sliding window to prevent model context overflow)
        val contentsArray = JSONArray()
        val windowedHistory = if (history.size > 14) history.takeLast(14) else history

        for (msg in windowedHistory) {
            val contentObj = JSONObject()
            val role = if (msg.role == "user") "user" else "model"
            contentObj.put("role", role)

            val partsArray = JSONArray()

            // Optional image if present
            if (!msg.imageBase64.isNullOrBlank()) {
                val inlineData = JSONObject()
                inlineData.put("mimeType", "image/jpeg")
                inlineData.put("data", msg.imageBase64)
                partsArray.put(JSONObject().put("inlineData", inlineData))
            }

            // Text part
            if (msg.text.isNotBlank()) {
                partsArray.put(JSONObject().put("text", msg.text))
            }

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
        }
        root.put("contents", contentsArray)

        // Generation Config
        val genConfig = JSONObject()
        genConfig.put("temperature", temperature)
        root.put("generationConfig", genConfig)

        return root
    }

    private fun extractTextFromResponse(json: JSONObject): String {
        val candidates = json.optJSONArray("candidates") ?: return ""
        if (candidates.length() == 0) return ""

        val candidate = candidates.getJSONObject(0)
        val content = candidate.optJSONObject("content") ?: return ""
        val parts = content.optJSONArray("parts") ?: return ""

        val sb = StringBuilder()
        for (i in 0 until parts.length()) {
            val part = parts.getJSONObject(i)
            val text = part.optString("text", "")
            if (text.isNotEmpty()) {
                sb.append(text)
            }
        }
        return sb.toString()
    }

    private fun formatErrorMessage(statusCode: Int, rawBody: String): String {
        return when (statusCode) {
            400 -> "Invalid request. Please rephrase or shorten your prompt."
            401, 403 -> "Invalid or unauthorized API key. Please check your Gemini API key in the Secrets panel."
            429 -> "API rate limit or quota exceeded. Please wait a moment and try again."
            500, 503 -> "The AI service is temporarily unavailable. Please try again shortly."
            else -> "KIGO AI couldn't connect to the AI service (Status $statusCode). Please try again."
        }
    }
}
