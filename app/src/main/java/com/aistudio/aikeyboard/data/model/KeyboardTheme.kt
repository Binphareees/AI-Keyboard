package com.aistudio.aikeyboard.data.model

import androidx.compose.ui.graphics.Color

enum class KeyboardThemeId(val displayName: String) {
    MIDNIGHT_NEON("Midnight Neon"),
    OBSIDIAN_DARK("Obsidian AMOLED"),
    CYBERPUNK_EMERALD("Cyberpunk Emerald"),
    SUNSET_ROSE("Sunset Rose"),
    CLEAN_LIGHT("Clean Light"),
    FROST_BLUE("Frost Blue")
}

data class KeyboardColorScheme(
    val themeId: KeyboardThemeId,
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyPressedBackground: Color,
    val actionKeyBackground: Color,
    val actionKeyPressedBackground: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accentColor: Color,
    val suggestionStripBackground: Color,
    val suggestionTextColor: Color,
    val suggestionDividerColor: Color,
    val isDark: Boolean = true
) {
    companion object {
        fun getTheme(themeId: KeyboardThemeId): KeyboardColorScheme {
            return when (themeId) {
                KeyboardThemeId.MIDNIGHT_NEON -> KeyboardColorScheme(
                    themeId = themeId,
                    keyboardBackground = Color(0xFF0F172A),
                    keyBackground = Color(0xFF1E293B),
                    keyPressedBackground = Color(0xFF334155),
                    actionKeyBackground = Color(0xFF2563EB),
                    actionKeyPressedBackground = Color(0xFF1D4ED8),
                    primaryText = Color(0xFFF8FAFC),
                    secondaryText = Color(0xFF94A3B8),
                    accentColor = Color(0xFF38BDF8),
                    suggestionStripBackground = Color(0xFF1E293B),
                    suggestionTextColor = Color(0xFFE2E8F0),
                    suggestionDividerColor = Color(0xFF334155),
                    isDark = true
                )
                KeyboardThemeId.OBSIDIAN_DARK -> KeyboardColorScheme(
                    themeId = themeId,
                    keyboardBackground = Color(0xFF000000),
                    keyBackground = Color(0xFF121212),
                    keyPressedBackground = Color(0xFF242424),
                    actionKeyBackground = Color(0xFF6366F1),
                    actionKeyPressedBackground = Color(0xFF4F46E5),
                    primaryText = Color(0xFFFFFFFF),
                    secondaryText = Color(0xFF888888),
                    accentColor = Color(0xFF818CF8),
                    suggestionStripBackground = Color(0xFF0A0A0A),
                    suggestionTextColor = Color(0xFFEEEEEE),
                    suggestionDividerColor = Color(0xFF222222),
                    isDark = true
                )
                KeyboardThemeId.CYBERPUNK_EMERALD -> KeyboardColorScheme(
                    themeId = themeId,
                    keyboardBackground = Color(0xFF062018),
                    keyBackground = Color(0xFF0C382A),
                    keyPressedBackground = Color(0xFF134E3A),
                    actionKeyBackground = Color(0xFF10B981),
                    actionKeyPressedBackground = Color(0xFF059669),
                    primaryText = Color(0xFFECFDF5),
                    secondaryText = Color(0xFF6EE7B7),
                    accentColor = Color(0xFF34D399),
                    suggestionStripBackground = Color(0xFF0C382A),
                    suggestionTextColor = Color(0xFFD1FAE5),
                    suggestionDividerColor = Color(0xFF134E3A),
                    isDark = true
                )
                KeyboardThemeId.SUNSET_ROSE -> KeyboardColorScheme(
                    themeId = themeId,
                    keyboardBackground = Color(0xFF1F1122),
                    keyBackground = Color(0xFF351C3B),
                    keyPressedBackground = Color(0xFF4C2754),
                    actionKeyBackground = Color(0xFFE11D48),
                    actionKeyPressedBackground = Color(0xFFBE123C),
                    primaryText = Color(0xFFFFF1F2),
                    secondaryText = Color(0xFFFDA4AF),
                    accentColor = Color(0xFFFB7185),
                    suggestionStripBackground = Color(0xFF351C3B),
                    suggestionTextColor = Color(0xFFFFE4E6),
                    suggestionDividerColor = Color(0xFF4C2754),
                    isDark = true
                )
                KeyboardThemeId.CLEAN_LIGHT -> KeyboardColorScheme(
                    themeId = themeId,
                    keyboardBackground = Color(0xFFE2E8F0),
                    keyBackground = Color(0xFFFFFFFF),
                    keyPressedBackground = Color(0xFFCBD5E1),
                    actionKeyBackground = Color(0xFF3B82F6),
                    actionKeyPressedBackground = Color(0xFF2563EB),
                    primaryText = Color(0xFF0F172A),
                    secondaryText = Color(0xFF64748B),
                    accentColor = Color(0xFF2563EB),
                    suggestionStripBackground = Color(0xFFF1F5F9),
                    suggestionTextColor = Color(0xFF1E293B),
                    suggestionDividerColor = Color(0xFFCBD5E1),
                    isDark = false
                )
                KeyboardThemeId.FROST_BLUE -> KeyboardColorScheme(
                    themeId = themeId,
                    keyboardBackground = Color(0xFF0B192C),
                    keyBackground = Color(0xFF1E3E62),
                    keyPressedBackground = Color(0xFF2B5B8C),
                    actionKeyBackground = Color(0xFF00ADB5),
                    actionKeyPressedBackground = Color(0xFF008E9B),
                    primaryText = Color(0xFFEEEEEE),
                    secondaryText = Color(0xFF90CAF9),
                    accentColor = Color(0xFF00ADB5),
                    suggestionStripBackground = Color(0xFF1E3E62),
                    suggestionTextColor = Color(0xFFE3F2FD),
                    suggestionDividerColor = Color(0xFF2B5B8C),
                    isDark = true
                )
            }
        }
    }
}
