package com.aistudio.aikeyboard.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.aistudio.aikeyboard.data.model.KeyboardLanguage
import com.aistudio.aikeyboard.data.model.KeyboardPreferences
import com.aistudio.aikeyboard.data.model.KeyboardThemeId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardPreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_keyboard_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<KeyboardPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): KeyboardPreferences {
        val themeName = prefs.getString(KEY_THEME, KeyboardThemeId.MIDNIGHT_NEON.name) ?: KeyboardThemeId.MIDNIGHT_NEON.name
        val themeId = try {
            KeyboardThemeId.valueOf(themeName)
        } catch (e: Exception) {
            KeyboardThemeId.MIDNIGHT_NEON
        }

        val langCode = prefs.getString(KEY_LANG, KeyboardLanguage.ENGLISH.code) ?: "en"
        val lang = KeyboardLanguage.entries.find { it.code == langCode } ?: KeyboardLanguage.ENGLISH

        return KeyboardPreferences(
            themeId = themeId,
            soundEnabled = prefs.getBoolean(KEY_SOUND, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION, true),
            hapticStrength = prefs.getInt(KEY_HAPTIC_STRENGTH, 30),
            autoCapitalization = prefs.getBoolean(KEY_AUTO_CAPS, true),
            showNumberRow = prefs.getBoolean(KEY_SHOW_NUMBERS, true),
            keyPopupEnabled = prefs.getBoolean(KEY_KEY_POPUP, true),
            smartSuggestionsEnabled = prefs.getBoolean(KEY_SUGGESTIONS, true),
            aiQuickBarEnabled = prefs.getBoolean(KEY_AI_QUICK_BAR, true),
            activeLanguage = lang,
            customApiKey = prefs.getString(KEY_CUSTOM_API_KEY, "") ?: "",
            keyboardHeightPercent = prefs.getInt(KEY_HEIGHT_PERCENT, 100)
        )
    }

    fun updatePreferences(transform: (KeyboardPreferences) -> KeyboardPreferences) {
        val newPrefs = transform(_preferences.value)
        prefs.edit().apply {
            putString(KEY_THEME, newPrefs.themeId.name)
            putBoolean(KEY_SOUND, newPrefs.soundEnabled)
            putBoolean(KEY_VIBRATION, newPrefs.vibrationEnabled)
            putInt(KEY_HAPTIC_STRENGTH, newPrefs.hapticStrength)
            putBoolean(KEY_AUTO_CAPS, newPrefs.autoCapitalization)
            putBoolean(KEY_SHOW_NUMBERS, newPrefs.showNumberRow)
            putBoolean(KEY_KEY_POPUP, newPrefs.keyPopupEnabled)
            putBoolean(KEY_SUGGESTIONS, newPrefs.smartSuggestionsEnabled)
            putBoolean(KEY_AI_QUICK_BAR, newPrefs.aiQuickBarEnabled)
            putString(KEY_LANG, newPrefs.activeLanguage.code)
            putString(KEY_CUSTOM_API_KEY, newPrefs.customApiKey)
            putInt(KEY_HEIGHT_PERCENT, newPrefs.keyboardHeightPercent)
        }.apply()
        _preferences.value = newPrefs
    }

    companion object {
        private const val KEY_THEME = "theme_id"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_HAPTIC_STRENGTH = "haptic_strength"
        private const val KEY_AUTO_CAPS = "auto_caps"
        private const val KEY_SHOW_NUMBERS = "show_numbers"
        private const val KEY_KEY_POPUP = "key_popup"
        private const val KEY_SUGGESTIONS = "suggestions_enabled"
        private const val KEY_AI_QUICK_BAR = "ai_quick_bar"
        private const val KEY_LANG = "active_language"
        private const val KEY_CUSTOM_API_KEY = "custom_api_key"
        private const val KEY_HEIGHT_PERCENT = "keyboard_height_percent"
    }
}
