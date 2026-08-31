package com.aistudio.aikeyboard.keyboard

enum class KeyboardLayer {
    ALPHABET,
    SYMBOLS,
    MORE_SYMBOLS,
    EMOJI,
    CLIPBOARD,
    AI_STUDIO
}

data class KeyboardState(
    val layer: KeyboardLayer = KeyboardLayer.ALPHABET,
    val isShifted: Boolean = false,
    val isCapsLock: Boolean = false,
    val composingText: String = "",
    val suggestions: List<String> = listOf("the", "to", "and", "a", "is", "in"),
    val isAiProcessing: Boolean = false,
    val aiResultText: String = "",
    val aiErrorMessage: String? = null,
    val previewKey: String? = null
)
