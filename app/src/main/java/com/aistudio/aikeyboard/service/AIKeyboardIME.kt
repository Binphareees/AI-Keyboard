package com.aistudio.aikeyboard.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aistudio.aikeyboard.AIKeyboardApp
import com.aistudio.aikeyboard.ai.AiEngine
import com.aistudio.aikeyboard.data.model.AiAction
import com.aistudio.aikeyboard.data.model.ClipboardItem
import com.aistudio.aikeyboard.data.model.KeyboardLanguage
import com.aistudio.aikeyboard.keyboard.KeyAction
import com.aistudio.aikeyboard.keyboard.KeyboardLayer
import com.aistudio.aikeyboard.keyboard.KeyboardState
import com.aistudio.aikeyboard.keyboard.SoundHapticManager
import com.aistudio.aikeyboard.keyboard.TextContextExtractor
import com.aistudio.aikeyboard.ui.components.KeyboardView
import com.aistudio.aikeyboard.ui.theme.AIKeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AIKeyboardIME : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private lateinit var soundHapticManager: SoundHapticManager
    private var keyboardState by mutableStateOf(KeyboardState())
    private var lastShiftPressTime = 0L

    private val preferenceManager by lazy { AIKeyboardApp.instance.preferenceManager }
    private val clipboardDao by lazy { AIKeyboardApp.instance.database.clipboardDao() }

    private var clipboardItems by mutableStateOf<List<ClipboardItem>>(emptyList())

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        soundHapticManager = SoundHapticManager(this)

        // Observe clipboard DB
        serviceScope.launch {
            clipboardDao.getAllItems().collectLatest { items ->
                clipboardItems = items
            }
        }

        // Listen for system clipboard changes
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.addPrimaryClipChangedListener {
            val clip = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()
                if (!text.isNullOrBlank()) {
                    serviceScope.launch(Dispatchers.IO) {
                        clipboardDao.insertItem(ClipboardItem(text = text))
                    }
                }
            }
        }
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        return ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@AIKeyboardIME)
            setViewTreeSavedStateRegistryOwner(this@AIKeyboardIME)
            setContent {
                val preferences by preferenceManager.preferences.collectAsState()

                AIKeyboardTheme(darkTheme = preferences.themeId.name != "CLEAN_LIGHT") {
                    KeyboardView(
                        state = keyboardState,
                        preferences = preferences,
                        clipboardItems = clipboardItems,
                        onKeyAction = { handleKeyAction(it) },
                        onAiAction = { handleAiAction(it) },
                        onOpenAiStudio = {
                            val currentText = getCurrentSurroundingText()
                            keyboardState = keyboardState.copy(
                                layer = KeyboardLayer.AI_STUDIO,
                                composingText = currentText
                            )
                        },
                        onInsertAiResult = { result ->
                            commitTextToTarget(result)
                        },
                        onClearAiResult = {
                            keyboardState = keyboardState.copy(aiResultText = "", aiErrorMessage = null)
                        },
                        onEmojiSelected = { emoji ->
                            commitTextToTarget(emoji)
                        },
                        onClipboardItemSelected = { clipText ->
                            commitTextToTarget(clipText)
                        },
                        onTogglePinClipboard = { item ->
                            serviceScope.launch(Dispatchers.IO) {
                                clipboardDao.updateItem(item.copy(isPinned = !item.isPinned))
                            }
                        },
                        onDeleteClipboardItem = { item ->
                            serviceScope.launch(Dispatchers.IO) {
                                clipboardDao.deleteItem(item)
                            }
                        },
                        onClearUnpinnedClipboard = {
                            serviceScope.launch(Dispatchers.IO) {
                                clipboardDao.clearUnpinned()
                            }
                        },
                        onSwitchLayer = { layer ->
                            keyboardState = keyboardState.copy(layer = layer)
                        }
                    )
                }
            }
        }
    }

    private fun handleKeyAction(action: KeyAction) {
        val prefs = preferenceManager.preferences.value
        soundHapticManager.playKeySound(prefs.soundEnabled)
        soundHapticManager.playHaptic(prefs.vibrationEnabled, prefs.hapticStrength)

        val ic = currentInputConnection

        when (action) {
            is KeyAction.Text -> {
                ic?.commitText(action.text, 1)
                // If shifted (not caps locked), reset shift back to lowercase
                if (keyboardState.isShifted && !keyboardState.isCapsLock) {
                    keyboardState = keyboardState.copy(isShifted = false)
                }
            }
            is KeyAction.Backspace -> {
                val selected = ic?.getSelectedText(0)
                if (!selected.isNullOrEmpty()) {
                    ic.commitText("", 1)
                } else {
                    ic?.deleteSurroundingText(1, 0)
                }
            }
            is KeyAction.Space -> {
                ic?.commitText(" ", 1)
            }
            is KeyAction.Enter -> {
                sendKeyChar('\n')
            }
            is KeyAction.Shift -> {
                val now = System.currentTimeMillis()
                if (now - lastShiftPressTime < 400) {
                    // Double tap: toggle caps lock
                    keyboardState = keyboardState.copy(
                        isCapsLock = !keyboardState.isCapsLock,
                        isShifted = !keyboardState.isCapsLock
                    )
                } else {
                    // Single tap: toggle shifted
                    val newShifted = !keyboardState.isShifted
                    keyboardState = keyboardState.copy(
                        isShifted = newShifted,
                        isCapsLock = false
                    )
                }
                lastShiftPressTime = now
            }
            is KeyAction.SwitchSymbols -> {
                keyboardState = keyboardState.copy(layer = KeyboardLayer.SYMBOLS)
            }
            is KeyAction.SwitchLetters -> {
                keyboardState = keyboardState.copy(layer = KeyboardLayer.ALPHABET)
            }
            is KeyAction.SwitchMoreSymbols -> {
                keyboardState = keyboardState.copy(layer = KeyboardLayer.MORE_SYMBOLS)
            }
            is KeyAction.OpenEmoji -> {
                keyboardState = keyboardState.copy(layer = KeyboardLayer.EMOJI)
            }
            is KeyAction.OpenClipboard -> {
                keyboardState = keyboardState.copy(layer = KeyboardLayer.CLIPBOARD)
            }
            is KeyAction.OpenAiToolbar -> {
                val currentText = getCurrentSurroundingText()
                keyboardState = keyboardState.copy(
                    layer = KeyboardLayer.AI_STUDIO,
                    composingText = currentText
                )
            }
            is KeyAction.OpenSettings -> {
                // Return to alphabet layer
                keyboardState = keyboardState.copy(layer = KeyboardLayer.ALPHABET)
            }
            is KeyAction.LanguageSwitch -> {
                val languages = KeyboardLanguage.entries
                val currentIndex = languages.indexOf(prefs.activeLanguage)
                val nextLang = languages[(currentIndex + 1) % languages.size]
                preferenceManager.updatePreferences { it.copy(activeLanguage = nextLang) }
            }
            is KeyAction.VoiceInput -> {
                // Fallback action
            }
        }
    }

    private var activeAiJob: kotlinx.coroutines.Job? = null

    private fun handleAiAction(action: AiAction) {
        val prefs = preferenceManager.preferences.value
        soundHapticManager.playKeySound(prefs.soundEnabled)
        soundHapticManager.playHaptic(prefs.vibrationEnabled, prefs.hapticStrength)

        if (!TextContextExtractor.isSafeForAi(currentInputEditorInfo)) {
            keyboardState = keyboardState.copy(
                aiErrorMessage = "AI is disabled on password and secure fields for your privacy."
            )
            return
        }

        val textToProcess = getCurrentSurroundingText()
        if (textToProcess.isBlank() && action != AiAction.SMART_REPLY) {
            keyboardState = keyboardState.copy(
                aiErrorMessage = "Type or select some text first."
            )
            return
        }

        activeAiJob?.cancel()

        keyboardState = keyboardState.copy(
            isAiProcessing = true,
            aiResultText = "",
            aiErrorMessage = null
        )

        activeAiJob = serviceScope.launch {
            val result = AiEngine.processText(
                inputText = textToProcess,
                action = action,
                targetLanguage = prefs.activeLanguage.displayName,
                userApiKey = prefs.customApiKey
            )
            result.onSuccess { output ->
                keyboardState = keyboardState.copy(
                    isAiProcessing = false,
                    aiResultText = output,
                    aiErrorMessage = null
                )
            }.onFailure { error ->
                keyboardState = keyboardState.copy(
                    isAiProcessing = false,
                    aiResultText = "",
                    aiErrorMessage = error.message ?: "AI request failed"
                )
            }
        }
    }

    private fun getCurrentSurroundingText(): String {
        return TextContextExtractor.extractSelectedOrCurrentText(
            currentInputConnection,
            currentInputEditorInfo
        )
    }

    private fun commitTextToTarget(text: String) {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText(text, 1)
        } else {
            ic.commitText(text, 1)
        }
        keyboardState = keyboardState.copy(aiResultText = "", aiErrorMessage = null)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        serviceScope.cancel()
    }
}
