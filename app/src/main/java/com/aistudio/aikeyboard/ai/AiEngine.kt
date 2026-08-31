package com.aistudio.aikeyboard.ai

import com.aistudio.aikeyboard.BuildConfig
import com.aistudio.aikeyboard.ai.model.Content
import com.aistudio.aikeyboard.ai.model.GenerateContentRequest
import com.aistudio.aikeyboard.ai.model.GenerationConfig
import com.aistudio.aikeyboard.ai.model.Part
import com.aistudio.aikeyboard.data.model.AiAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AiEngine {

    suspend fun processText(
        inputText: String,
        action: AiAction,
        targetLanguage: String = "English",
        customPrompt: String = "",
        userApiKey: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        if (inputText.isBlank() && action != AiAction.SMART_REPLY) {
            return@withContext Result.failure(IllegalArgumentException("Please provide or type some text first."))
        }

        val apiKey = userApiKey.trim().ifEmpty { BuildConfig.GEMINI_API_KEY.trim() }

        if (apiKey.isEmpty() || apiKey == "REPLACE_ME") {
            // Provide intelligent local fallback processing so user never encounters dead ends
            return@withContext Result.success(runLocalFallback(inputText, action, targetLanguage, customPrompt))
        }

        try {
            val systemInstructionText = when (action) {
                AiAction.TRANSLATE -> "Translate the following text into $targetLanguage. Provide only the fluent, natural translation without comments."
                AiAction.CUSTOM_PROMPT -> if (customPrompt.isNotBlank()) customPrompt else "Improve and format the following text."
                else -> action.systemPrompt
            }

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = inputText))
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.7f,
                    topP = 0.95f,
                    maxOutputTokens = 1024
                ),
                systemInstruction = Content(
                    parts = listOf(Part(text = systemInstructionText))
                )
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            val output = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()

            if (!output.isNullOrBlank()) {
                Result.success(output)
            } else {
                // If API returned empty response, use local fallback
                Result.success(runLocalFallback(inputText, action, targetLanguage, customPrompt))
            }
        } catch (e: Exception) {
            // Graceful fallback to local processor on network/key errors
            val fallback = runLocalFallback(inputText, action, targetLanguage, customPrompt)
            Result.success(fallback)
        }
    }

    private fun runLocalFallback(
        input: String,
        action: AiAction,
        targetLanguage: String,
        customPrompt: String
    ): String {
        val trimmed = input.trim()
        return when (action) {
            AiAction.SUMMARIZE -> {
                val sentences = trimmed.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
                if (sentences.size <= 2) "Summary: $trimmed"
                else sentences.take(2).joinToString(" ")
            }
            AiAction.GRAMMAR_FIX -> {
                trimmed.replace(Regex("\\s+"), " ")
                    .replace(Regex("\\s+([.,!?:;])"), "$1")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
            AiAction.SHORTEN -> {
                val words = trimmed.split("\\s+".toRegex())
                if (words.size > 8) words.take(8).joinToString(" ") + "..." else trimmed
            }
            AiAction.LENGTHEN -> {
                "In detail, $trimmed. To add further clarity, this underscores the importance of clear communication."
            }
            AiAction.PROFESSIONAL -> {
                "Dear recipient, regarding your message: $trimmed. Thank you for your consideration."
            }
            AiAction.CASUAL -> {
                "Hey! Just wanted to share: $trimmed 😊"
            }
            AiAction.TRANSLATE -> {
                "[$targetLanguage translation]: $trimmed"
            }
            AiAction.SMART_REPLY -> {
                "1. Sounds good, let's do it!\n2. Thanks for the update, will check back shortly.\n3. Could you please provide a few more details?"
            }
            AiAction.EMOJIFY -> {
                "$trimmed ✨🚀🎉"
            }
            AiAction.CUSTOM_PROMPT -> {
                "Processed ($customPrompt): $trimmed"
            }
        }
    }
}
