package com.aistudio.aikeyboard.keyboard

sealed class KeyAction {
    data class Text(val text: String) : KeyAction()
    object Backspace : KeyAction()
    object Space : KeyAction()
    object Enter : KeyAction()
    object Shift : KeyAction()
    object SwitchSymbols : KeyAction()
    object SwitchLetters : KeyAction()
    object SwitchMoreSymbols : KeyAction()
    object OpenEmoji : KeyAction()
    object OpenClipboard : KeyAction()
    object OpenAiToolbar : KeyAction()
    object OpenSettings : KeyAction()
    object LanguageSwitch : KeyAction()
    object VoiceInput : KeyAction()
}

data class KeyModel(
    val primaryLabel: String,
    val secondaryLabel: String = "",
    val action: KeyAction,
    val weight: Float = 1.0f,
    val isFunctional: Boolean = false,
    val isAccent: Boolean = false
)
