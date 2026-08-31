package com.aistudio.aikeyboard.data.model

enum class KeyboardLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HAUSA("ha", "Hausa"),
    SPANISH("es", "Spanish"),
    FRENCH("fr", "French"),
    GERMAN("de", "German"),
    ARABIC("ar", "Arabic")
}

data class KeyboardPreferences(
    val themeId: KeyboardThemeId = KeyboardThemeId.MIDNIGHT_NEON,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val hapticStrength: Int = 30, // ms or amplitude
    val autoCapitalization: Boolean = true,
    val showNumberRow: Boolean = true,
    val keyPopupEnabled: Boolean = true,
    val smartSuggestionsEnabled: Boolean = true,
    val aiQuickBarEnabled: Boolean = true,
    val activeLanguage: KeyboardLanguage = KeyboardLanguage.ENGLISH,
    val customApiKey: String = "",
    val keyboardHeightPercent: Int = 100 // 80% to 120%
)
