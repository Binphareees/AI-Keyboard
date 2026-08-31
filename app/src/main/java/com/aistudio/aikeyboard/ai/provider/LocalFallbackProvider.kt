package com.aistudio.aikeyboard.ai.provider

class LocalFallbackProvider : AIProvider {

    override val name: String = "Local Heuristic Engine"
    override val providerType: AIProviderType = AIProviderType.LOCAL_FALLBACK

    override suspend fun isConfigured(): Boolean = true

    override suspend fun generateText(request: AIRequest): AIResponse {
        val prompt = request.prompt
        val system = request.systemInstruction?.lowercase() ?: ""

        val transformed = when {
            system.contains("fix") || system.contains("grammar") -> {
                prompt.trim()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    .let { if (!it.endsWith(".") && !it.endsWith("!") && !it.endsWith("?")) "$it." else it }
                    .replace(Regex("\\bi\\b"), "I")
                    .replace(Regex("\\s+"), " ")
            }
            system.contains("professional") -> {
                "Dear recipient,\n\n${prompt.trim().replaceFirstChar { it.uppercase() }}.\n\nBest regards."
            }
            system.contains("casual") -> {
                "Hey! ${prompt.trim().lowercase().replaceFirstChar { it.uppercase() }} 😊"
            }
            system.contains("short") || system.contains("concise") -> {
                prompt.trim().split(". ").firstOrNull()?.let { "$it." } ?: prompt.trim()
            }
            system.contains("elaborate") || system.contains("expand") -> {
                "${prompt.trim()}. Furthermore, this ensures clarity, precision, and comprehensive context for all parties involved."
            }
            system.contains("summarize") -> {
                "• ${prompt.trim().replace("\n", "\n• ")}"
            }
            system.contains("emoji") -> {
                "${prompt.trim()} ✨🚀🎯"
            }
            else -> {
                prompt.trim()
            }
        }

        return AIResponse(
            text = transformed,
            isSuccess = true,
            providerName = name
        )
    }
}
