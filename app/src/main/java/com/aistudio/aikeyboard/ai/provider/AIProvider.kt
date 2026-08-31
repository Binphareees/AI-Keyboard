package com.aistudio.aikeyboard.ai.provider

data class AIRequest(
    val prompt: String,
    val systemInstruction: String? = null,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1024
)

data class AIResponse(
    val text: String,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null,
    val providerName: String = "Unknown"
)

enum class AIProviderType(val displayName: String) {
    GEMINI("Google Gemini"),
    OPENAI_COMPATIBLE("OpenAI Compatible"),
    LOCAL_FALLBACK("Local Smart Engine")
}

interface AIProvider {
    val name: String
    val providerType: AIProviderType
    suspend fun generateText(request: AIRequest): AIResponse
    suspend fun isConfigured(): Boolean
}
