package com.example.ai

data class AIChatMessage(
    val role: String, // "user" or "model"
    val text: String,
    val imageBase64: String? = null
)

sealed class AIResult<out T> {
    data class Success<T>(val data: T) : AIResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : AIResult<Nothing>()
}

enum class EducationMode(val label: String, val instruction: String) {
    DEFAULT("Standard", ""),
    CLASS_12("Class 12 / High School", "Provide explanations tailored for Class 12 high school students, with clear conceptual rigor, relevant formulas, derivations, and formal definitions."),
    STEP_BY_STEP("Step-by-Step", "Structure all explanations and problem solutions strictly step-by-step with clear numbered reasoning."),
    CONCISE("Concise / Direct", "Keep explanations extremely concise, providing only the direct answer and essential logic without filler."),
    WITH_EXAMPLES("With Real Examples", "Include clear, relatable practical examples for every concept explained.")
}

enum class GeminiModel(val modelId: String, val displayName: String, val description: String) {
    GEMINI_3_5_FLASH("gemini-3.5-flash", "Gemini 3.5 Flash", "Ultra-fast, high-intelligence default for reasoning, coding, math & vision"),
    GEMINI_3_1_PRO("gemini-3.1-pro-preview", "Gemini 3.1 Pro", "Advanced reasoning for complex STEM, deep mathematics, and architecture"),
    GEMINI_2_5_FLASH_IMAGE("gemini-2.5-flash-image", "Gemini 2.5 Flash Image", "High-fidelity native generative image model")
}
