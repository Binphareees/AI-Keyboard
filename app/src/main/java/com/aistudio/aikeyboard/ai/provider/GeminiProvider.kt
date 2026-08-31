package com.aistudio.aikeyboard.ai.provider

import com.aistudio.aikeyboard.BuildConfig
import com.aistudio.aikeyboard.ai.RetrofitClient
import com.aistudio.aikeyboard.ai.model.Content
import com.aistudio.aikeyboard.ai.model.GenerateContentRequest
import com.aistudio.aikeyboard.ai.model.GenerationConfig
import com.aistudio.aikeyboard.ai.model.Part
import com.aistudio.aikeyboard.data.preferences.KeyboardPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiProvider(
    private val preferenceManager: KeyboardPreferenceManager
) : AIProvider {

    override val name: String = "Google Gemini"
    override val providerType: AIProviderType = AIProviderType.GEMINI

    override suspend fun isConfigured(): Boolean {
        val key = preferenceManager.preferences.value.customApiKey.trim()
            .ifEmpty { BuildConfig.GEMINI_API_KEY.trim() }
        return key.isNotBlank() && key != "REPLACE_ME"
    }

    override suspend fun generateText(request: AIRequest): AIResponse = withContext(Dispatchers.IO) {
        val apiKey = preferenceManager.preferences.value.customApiKey.trim()
            .ifEmpty { BuildConfig.GEMINI_API_KEY.trim() }

        if (apiKey.isBlank() || apiKey == "REPLACE_ME") {
            return@withContext AIResponse(
                text = "",
                isSuccess = false,
                errorMessage = "Gemini API key is not configured. Please add it in AI Keyboard Settings.",
                providerName = name
            )
        }

        try {
            val systemInstruction = request.systemInstruction?.let {
                Content(parts = listOf(Part(text = it)))
            }

            val body = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = request.prompt))
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = request.temperature,
                    maxOutputTokens = request.maxTokens
                ),
                systemInstruction = systemInstruction
            )

            val response = RetrofitClient.service.generateContent(
                apiKey = apiKey,
                request = body
            )

            val candidateText = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text?.trim()

            if (!candidateText.isNullOrBlank()) {
                AIResponse(
                    text = candidateText,
                    isSuccess = true,
                    providerName = name
                )
            } else {
                AIResponse(
                    text = "",
                    isSuccess = false,
                    errorMessage = "Gemini returned an empty response.",
                    providerName = name
                )
            }
        } catch (e: Exception) {
            AIResponse(
                text = "",
                isSuccess = false,
                errorMessage = "Network error: ${e.localizedMessage ?: "Unknown error"}",
                providerName = name
            )
        }
    }
}
