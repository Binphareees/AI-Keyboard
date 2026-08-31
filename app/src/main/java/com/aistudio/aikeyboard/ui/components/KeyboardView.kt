package com.aistudio.aikeyboard.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.aikeyboard.data.model.AiAction
import com.aistudio.aikeyboard.data.model.ClipboardItem
import com.aistudio.aikeyboard.data.model.KeyboardColorScheme
import com.aistudio.aikeyboard.data.model.KeyboardLanguage
import com.aistudio.aikeyboard.data.model.KeyboardPreferences
import com.aistudio.aikeyboard.keyboard.KeyAction
import com.aistudio.aikeyboard.keyboard.KeyboardLayer
import com.aistudio.aikeyboard.keyboard.KeyboardLayouts
import com.aistudio.aikeyboard.keyboard.KeyboardState

@Composable
fun KeyboardView(
    state: KeyboardState,
    preferences: KeyboardPreferences,
    clipboardItems: List<ClipboardItem>,
    onKeyAction: (KeyAction) -> Unit,
    onAiAction: (AiAction) -> Unit,
    onOpenAiStudio: () -> Unit,
    onInsertAiResult: (String) -> Unit,
    onClearAiResult: () -> Unit,
    onEmojiSelected: (String) -> Unit,
    onClipboardItemSelected: (String) -> Unit,
    onTogglePinClipboard: (ClipboardItem) -> Unit,
    onDeleteClipboardItem: (ClipboardItem) -> Unit,
    onClearUnpinnedClipboard: () -> Unit,
    onSwitchLayer: (KeyboardLayer) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = remember(preferences.themeId) {
        KeyboardColorScheme.getTheme(preferences.themeId)
    }

    val heightFactor = preferences.keyboardHeightPercent / 100f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.keyboardBackground)
            .testTag("ai_keyboard_view")
    ) {
        // AI Quick Toolbar / Suggestion Strip
        if (preferences.aiQuickBarEnabled) {
            AiQuickToolbar(
                colorScheme = colorScheme,
                isProcessing = state.isAiProcessing,
                resultText = state.aiResultText,
                errorMessage = state.aiErrorMessage,
                onActionClick = onAiAction,
                onOpenFullStudio = onOpenAiStudio,
                onInsertResult = onInsertAiResult,
                onClearResult = onClearAiResult
            )
        } else if (preferences.smartSuggestionsEnabled && state.layer != KeyboardLayer.EMOJI && state.layer != KeyboardLayer.CLIPBOARD && state.layer != KeyboardLayer.AI_STUDIO) {
            // Standard Word Suggestions Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(colorScheme.suggestionStripBackground)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.suggestions.forEach { word ->
                    Surface(
                        onClick = { onKeyAction(KeyAction.Text(word + " ")) },
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Transparent,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = word,
                                color = colorScheme.suggestionTextColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Layer Switch Content
        AnimatedContent(
            targetState = state.layer,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "keyboard_layer_transition"
        ) { layer ->
            when (layer) {
                KeyboardLayer.ALPHABET -> {
                    val rows = KeyboardLayouts.getAlphabetRows(
                        language = preferences.activeLanguage,
                        isShifted = state.isShifted,
                        isCapsLock = state.isCapsLock,
                        showNumberRow = preferences.showNumberRow
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        rows.forEach { rowKeys ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((48 * heightFactor).dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                rowKeys.forEach { key ->
                                    KeyButton(
                                        keyModel = key,
                                        colorScheme = colorScheme,
                                        modifier = Modifier.weight(key.weight),
                                        onKeyPress = onKeyAction
                                    )
                                }
                            }
                        }
                    }
                }

                KeyboardLayer.SYMBOLS -> {
                    val rows = KeyboardLayouts.getSymbolsRows()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        rows.forEach { rowKeys ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((48 * heightFactor).dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                rowKeys.forEach { key ->
                                    KeyButton(
                                        keyModel = key,
                                        colorScheme = colorScheme,
                                        modifier = Modifier.weight(key.weight),
                                        onKeyPress = onKeyAction
                                    )
                                }
                            }
                        }
                    }
                }

                KeyboardLayer.MORE_SYMBOLS -> {
                    val rows = KeyboardLayouts.getMoreSymbolsRows()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        rows.forEach { rowKeys ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((48 * heightFactor).dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                rowKeys.forEach { key ->
                                    KeyButton(
                                        keyModel = key,
                                        colorScheme = colorScheme,
                                        modifier = Modifier.weight(key.weight),
                                        onKeyPress = onKeyAction
                                    )
                                }
                            }
                        }
                    }
                }

                KeyboardLayer.EMOJI -> {
                    EmojiPicker(
                        colorScheme = colorScheme,
                        onEmojiSelected = onEmojiSelected,
                        onBackspace = { onKeyAction(KeyAction.Backspace) },
                        onBackToKeyboard = { onSwitchLayer(KeyboardLayer.ALPHABET) }
                    )
                }

                KeyboardLayer.CLIPBOARD -> {
                    ClipboardManagerSheet(
                        colorScheme = colorScheme,
                        items = clipboardItems,
                        onItemClick = onClipboardItemSelected,
                        onTogglePin = onTogglePinClipboard,
                        onDeleteItem = onDeleteClipboardItem,
                        onClearUnpinned = onClearUnpinnedClipboard,
                        onBackToKeyboard = { onSwitchLayer(KeyboardLayer.ALPHABET) }
                    )
                }

                KeyboardLayer.AI_STUDIO -> {
                    AiStudioSheet(
                        colorScheme = colorScheme,
                        initialInput = state.composingText,
                        onInsertText = onInsertAiResult,
                        onBackToKeyboard = { onSwitchLayer(KeyboardLayer.ALPHABET) },
                        customApiKey = preferences.customApiKey
                    )
                }
            }
        }
    }
}
