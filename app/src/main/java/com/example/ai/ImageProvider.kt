package com.example.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeneratedImageResult(
    val imageBase64: String,
    val mimeType: String = "image/png",
    val caption: String? = null
)

class ImageProvider {
    companion object {
        private const val TAG = "ImageProvider"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
        private const val IMAGE_MODEL = "gemini-2.5-flash-image"
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    val isConfigured: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    suspend fun generateImage(
        prompt: String,
        aspectRatio: String = "1:1" // "1:1", "16:9", "4:3", "9:16"
    ): AIResult<GeneratedImageResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!isConfigured) {
            return@withContext AIResult.Error("GEMINI_API_KEY is not configured. Please configure it in AI Studio Secrets panel.")
        }

        try {
            val root = JSONObject()

            // Contents
            val contents = JSONArray()
            val content = JSONObject()
            val parts = JSONArray()
            parts.put(JSONObject().put("text", prompt))
            content.put("parts", parts)
            contents.put(content)
            root.put("contents", contents)

            // Generation config with IMAGE modality and imageConfig
            val genConfig = JSONObject()
            val modalities = JSONArray()
            modalities.put("TEXT")
            modalities.put("IMAGE")
            genConfig.put("responseModalities", modalities)

            val imgConfig = JSONObject()
            imgConfig.put("aspectRatio", aspectRatio)
            genConfig.put("imageConfig", imgConfig)
            root.put("generationConfig", genConfig)

            val url = "$BASE_URL$IMAGE_MODEL:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(root.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Image Gen failed: ${response.code} $responseBody")
                return@withContext AIResult.Error(
                    when (response.code) {
                        400 -> "Invalid image generation request. Please modify your prompt."
                        401, 403 -> "Unauthorized: Please verify your Gemini API key in Secrets panel."
                        429 -> "Image generation quota exceeded. Please wait a moment and try again."
                        else -> "Failed to generate image (Status ${response.code})."
                    }
                )
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext AIResult.Error("No image candidates returned by Gemini.")
            }

            val candidateParts = candidates.getJSONObject(0)
                .optJSONObject("content")
                ?.optJSONArray("parts")

            if (candidateParts == null || candidateParts.length() == 0) {
                return@withContext AIResult.Error("Empty content in image generation response.")
            }

            var foundImageBase64: String? = null
            var mimeType = "image/png"
            var textCaption: String? = null

            for (i in 0 until candidateParts.length()) {
                val part = candidateParts.getJSONObject(i)
                if (part.has("inlineData")) {
                    val inline = part.getJSONObject("inlineData")
                    foundImageBase64 = inline.optString("data", "")
                    mimeType = inline.optString("mimeType", "image/png")
                } else if (part.has("text")) {
                    textCaption = part.optString("text")
                }
            }

            if (!foundImageBase64.isNullOrBlank()) {
                AIResult.Success(
                    GeneratedImageResult(
                        imageBase64 = foundImageBase64,
                        mimeType = mimeType,
                        caption = textCaption
                    )
                )
            } else {
                val fallbackText = textCaption ?: "The model did not return image data."
                AIResult.Error("Image generation model response: $fallbackText")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating image", e)
            AIResult.Error("Network error during image generation: ${e.localizedMessage ?: "Unknown error"}", e)
        }
    }
}
