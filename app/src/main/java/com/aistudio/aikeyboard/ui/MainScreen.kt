package com.aistudio.aikeyboard.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.aikeyboard.AIKeyboardApp
import com.aistudio.aikeyboard.ai.AiEngine
import com.aistudio.aikeyboard.data.model.AiAction
import com.aistudio.aikeyboard.data.model.ClipboardItem
import com.aistudio.aikeyboard.data.model.KeyboardColorScheme
import com.aistudio.aikeyboard.data.model.KeyboardLanguage
import com.aistudio.aikeyboard.data.model.KeyboardThemeId
import com.aistudio.aikeyboard.keyboard.KeyAction
import com.aistudio.aikeyboard.keyboard.KeyboardLayer
import com.aistudio.aikeyboard.keyboard.KeyboardState
import com.aistudio.aikeyboard.keyboard.SoundHapticManager
import com.aistudio.aikeyboard.ui.components.KeyboardView
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PLAYGROUND("Playground", Icons.Default.Keyboard),
    AI_STUDIO("AI Studio", Icons.Default.AutoAwesome),
    CLIPBOARD("Clipboard", Icons.Default.ContentPaste),
    THEMES("Themes & Settings", Icons.Default.Tune),
    ACTIVATION("Setup", Icons.Default.CheckCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    app: AIKeyboardApp,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MainTab.PLAYGROUND) }
    val preferences by app.preferenceManager.preferences.collectAsState()
    val clipboardDao = app.database.clipboardDao()
    val clipboardItems by clipboardDao.getAllItems().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val soundHapticManager = remember { SoundHapticManager(context) }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("main_screen_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF38BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Keyboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Smart Typing & Gemini Assistant",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                contentColor = Color(0xFF94A3B8)
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0F172A),
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF38BDF8),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name}")
                    )
                }
            }
        },
        containerColor = Color(0xFF0B0F19)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.PLAYGROUND -> PlaygroundTab(
                    app = app,
                    clipboardItems = clipboardItems,
                    soundHapticManager = soundHapticManager
                )
                MainTab.AI_STUDIO -> AiStudioTab(app = app)
                MainTab.CLIPBOARD -> ClipboardTab(
                    app = app,
                    items = clipboardItems
                )
                MainTab.THEMES -> ThemesAndSettingsTab(app = app)
                MainTab.ACTIVATION -> ActivationTab(context = context)
            }
        }
    }
}

@Composable
fun PlaygroundTab(
    app: AIKeyboardApp,
    clipboardItems: List<ClipboardItem>,
    soundHapticManager: SoundHapticManager
) {
    val preferences by app.preferenceManager.preferences.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var playgroundText by remember { mutableStateOf("Hey team, here is the draft for our upcoming product launch. Please review and let me know if we need to adjust anything.") }
    var keyboardState by remember { mutableStateOf(KeyboardState()) }
    var lastShiftTime by remember { mutableStateOf(0L) }

    fun handleKeyAction(action: KeyAction) {
        soundHapticManager.playKeySound(preferences.soundEnabled)
        soundHapticManager.playHaptic(preferences.vibrationEnabled, preferences.hapticStrength)

        when (action) {
            is KeyAction.Text -> {
                playgroundText += action.text
                if (keyboardState.isShifted && !keyboardState.isCapsLock) {
                    keyboardState = keyboardState.copy(isShifted = false)
                }
            }
            is KeyAction.Backspace -> {
                if (playgroundText.isNotEmpty()) {
                    playgroundText = playgroundText.dropLast(1)
                }
            }
            is KeyAction.Space -> {
                playgroundText += " "
            }
            is KeyAction.Enter -> {
                playgroundText += "\n"
            }
            is KeyAction.Shift -> {
                val now = System.currentTimeMillis()
                if (now - lastShiftTime < 400) {
                    keyboardState = keyboardState.copy(
                        isCapsLock = !keyboardState.isCapsLock,
                        isShifted = !keyboardState.isCapsLock
                    )
                } else {
                    keyboardState = keyboardState.copy(
                        isShifted = !keyboardState.isShifted,
                        isCapsLock = false
                    )
                }
                lastShiftTime = now
            }
            is KeyAction.SwitchSymbols -> keyboardState = keyboardState.copy(layer = KeyboardLayer.SYMBOLS)
            is KeyAction.SwitchLetters -> keyboardState = keyboardState.copy(layer = KeyboardLayer.ALPHABET)
            is KeyAction.SwitchMoreSymbols -> keyboardState = keyboardState.copy(layer = KeyboardLayer.MORE_SYMBOLS)
            is KeyAction.OpenEmoji -> keyboardState = keyboardState.copy(layer = KeyboardLayer.EMOJI)
            is KeyAction.OpenClipboard -> keyboardState = keyboardState.copy(layer = KeyboardLayer.CLIPBOARD)
            is KeyAction.OpenAiToolbar -> {
                keyboardState = keyboardState.copy(layer = KeyboardLayer.AI_STUDIO, composingText = playgroundText)
            }
            is KeyAction.OpenSettings -> keyboardState = keyboardState.copy(layer = KeyboardLayer.ALPHABET)
            is KeyAction.LanguageSwitch -> {
                val langs = KeyboardLanguage.entries
                val idx = langs.indexOf(preferences.activeLanguage)
                val next = langs[(idx + 1) % langs.size]
                app.preferenceManager.updatePreferences { it.copy(activeLanguage = next) }
            }
            is KeyAction.VoiceInput -> {}
        }
    }

    fun executeAiAction(action: AiAction) {
        soundHapticManager.playKeySound(preferences.soundEnabled)
        soundHapticManager.playHaptic(preferences.vibrationEnabled, preferences.hapticStrength)

        if (playgroundText.isBlank() && action != AiAction.SMART_REPLY) {
            keyboardState = keyboardState.copy(aiErrorMessage = "Type some text in the box above first.")
            return
        }

        keyboardState = keyboardState.copy(isAiProcessing = true, aiResultText = "", aiErrorMessage = null)

        coroutineScope.launch {
            val result = AiEngine.processText(
                inputText = playgroundText,
                action = action,
                targetLanguage = preferences.activeLanguage.displayName,
                userApiKey = preferences.customApiKey
            )
            result.onSuccess { output ->
                keyboardState = keyboardState.copy(isAiProcessing = false, aiResultText = output, aiErrorMessage = null)
            }.onFailure { err ->
                keyboardState = keyboardState.copy(isAiProcessing = false, aiErrorMessage = err.message ?: "AI failed")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        // Playground Header & Live Input Field
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Interactive Playground",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Row {
                    Text(
                        text = "${playgroundText.length} chars",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${playgroundText.split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = playgroundText,
                onValueChange = { playgroundText = it },
                placeholder = { Text("Tap the keyboard below or type here...", color = Color(0xFF64748B)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF131C2E),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color(0xFFF8FAFC),
                    unfocusedTextColor = Color(0xFFF8FAFC)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("playground_text_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Preset Prompts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { playgroundText = "Could u pls check if this email sound good and polite for my boss: im leaving early tmrw" },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text("Draft Email", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
                OutlinedButton(
                    onClick = { playgroundText = "The artificial intelligence revolution is transforming how we communicate, work, and create content across mobile platforms." },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text("Article Intro", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
                OutlinedButton(
                    onClick = { playgroundText = "" },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Clear", fontSize = 10.sp, color = Color(0xFFF43F5E))
                }
            }
        }

        // Live Keyboard View embedded in Playground
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = Color(0xFF0F172A)
        ) {
            KeyboardView(
                state = keyboardState,
                preferences = preferences,
                clipboardItems = clipboardItems,
                onKeyAction = { handleKeyAction(it) },
                onAiAction = { executeAiAction(it) },
                onOpenAiStudio = {
                    keyboardState = keyboardState.copy(layer = KeyboardLayer.AI_STUDIO, composingText = playgroundText)
                },
                onInsertAiResult = { res ->
                    playgroundText = res
                    keyboardState = keyboardState.copy(aiResultText = "", aiErrorMessage = null)
                },
                onClearAiResult = {
                    keyboardState = keyboardState.copy(aiResultText = "", aiErrorMessage = null)
                },
                onEmojiSelected = { emoji ->
                    playgroundText += emoji
                },
                onClipboardItemSelected = { clip ->
                    playgroundText += clip
                },
                onTogglePinClipboard = { item ->
                    coroutineScope.launch {
                        app.database.clipboardDao().updateItem(item.copy(isPinned = !item.isPinned))
                    }
                },
                onDeleteClipboardItem = { item ->
                    coroutineScope.launch {
                        app.database.clipboardDao().deleteItem(item)
                    }
                },
                onClearUnpinnedClipboard = {
                    coroutineScope.launch {
                        app.database.clipboardDao().clearUnpinned()
                    }
                },
                onSwitchLayer = { layer ->
                    keyboardState = keyboardState.copy(layer = layer)
                }
            )
        }
    }
}

@Composable
fun AiStudioTab(app: AIKeyboardApp) {
    val preferences by app.preferenceManager.preferences.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var studioInput by remember { mutableStateOf("Thanks for reaching out! We received your bug report regarding the keyboard responsiveness on Android 15. Our engineering team is currently investigating the issue and we will provide an update within 24 hours.") }
    var selectedAction by remember { mutableStateOf(AiAction.PROFESSIONAL) }
    var selectedLang by remember { mutableStateOf(KeyboardLanguage.ENGLISH) }
    var customPrompt by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCopied by remember { mutableStateOf(false) }

    fun runStudioAi() {
        if (studioInput.isBlank() && selectedAction != AiAction.SMART_REPLY) {
            errorMessage = "Please enter some text first."
            return
        }
        isLoading = true
        errorMessage = null
        outputText = ""
        isCopied = false

        coroutineScope.launch {
            val result = AiEngine.processText(
                inputText = studioInput,
                action = selectedAction,
                targetLanguage = selectedLang.displayName,
                customPrompt = customPrompt,
                userApiKey = preferences.customApiKey
            )
            isLoading = false
            result.onSuccess { output ->
                outputText = output
            }.onFailure { err ->
                errorMessage = err.message ?: "Failed to generate AI response"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "AI Writing & Tone Studio",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Transform, polish, and translate any text powered by Gemini 3.5 Flash",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Actions grid
        Text(
            text = "Select Transformation:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(AiAction.GRAMMAR_FIX, AiAction.SUMMARIZE, AiAction.SHORTEN).forEach { act ->
                    val isSelected = act == selectedAction
                    Surface(
                        onClick = { selectedAction = act },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = act.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F172A) else Color.White
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(AiAction.PROFESSIONAL, AiAction.CASUAL, AiAction.LENGTHEN).forEach { act ->
                    val isSelected = act == selectedAction
                    Surface(
                        onClick = { selectedAction = act },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = act.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F172A) else Color.White
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(AiAction.TRANSLATE, AiAction.EMOJIFY, AiAction.SMART_REPLY, AiAction.CUSTOM_PROMPT).forEach { act ->
                    val isSelected = act == selectedAction
                    Surface(
                        onClick = { selectedAction = act },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = act.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F172A) else Color.White
                            )
                        }
                    }
                }
            }
        }

        // Custom Prompt input if selected
        if (selectedAction == AiAction.CUSTOM_PROMPT) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = customPrompt,
                onValueChange = { customPrompt = it },
                label = { Text("Custom AI Instruction", color = Color(0xFF94A3B8)) },
                placeholder = { Text("e.g., Rewrite this in Elizabethan Shakespearean style", color = Color(0xFF64748B)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF131C2E),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color(0xFFF8FAFC),
                    unfocusedTextColor = Color(0xFFF8FAFC)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Input Text:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = studioInput,
            onValueChange = { studioInput = it },
            placeholder = { Text("Enter text to transform...", color = Color(0xFF64748B)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedContainerColor = Color(0xFF131C2E),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedTextColor = Color(0xFFF8FAFC),
                unfocusedTextColor = Color(0xFFF8FAFC)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { runStudioAi() },
            enabled = !isLoading,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("btn_studio_execute")
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating with Gemini...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run ${selectedAction.title}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage ?: "", color = Color(0xFFF43F5E), fontSize = 12.sp)
        }

        if (outputText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Result Output",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(outputText))
                                    isCopied = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = if (isCopied) Color(0xFF10B981) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = outputText,
                        color = Color(0xFFF8FAFC),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ClipboardTab(
    app: AIKeyboardApp,
    items: List<ClipboardItem>
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items
        else items.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clipboard History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${items.size} total copied items saved in Room DB",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            if (items.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            app.database.clipboardDao().clearUnpinned()
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Clear Unpinned", fontSize = 11.sp, color = Color(0xFFF43F5E))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search your clips...", color = Color(0xFF64748B)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedContainerColor = Color(0xFF131C2E),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedTextColor = Color(0xFFF8FAFC),
                unfocusedTextColor = Color(0xFFF8FAFC)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No matching clips found" else "No clipboard history yet.\nCopy any text to automatically capture it here.",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isPinned) Color(0xFF1E2E48) else Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.text,
                                    color = Color(0xFFF8FAFC),
                                    fontSize = 13.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (item.isPinned) "📌 Pinned" else "Copied snippet",
                                    fontSize = 10.sp,
                                    color = if (item.isPinned) Color(0xFF38BDF8) else Color(0xFF64748B)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(item.text))
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            app.database.clipboardDao().updateItem(item.copy(isPinned = !item.isPinned))
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        if (item.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin",
                                        tint = if (item.isPinned) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            app.database.clipboardDao().deleteItem(item)
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemesAndSettingsTab(app: AIKeyboardApp) {
    val preferences by app.preferenceManager.preferences.collectAsState()
    val themes = KeyboardThemeId.entries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Themes & Customization",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Personalize colors, sound, haptic feedback and AI settings",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Themes Section
        Text(
            text = "Keyboard Theme Palette:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            themes.chunked(2).forEach { rowThemes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowThemes.forEach { themeId ->
                        val isSelected = themeId == preferences.themeId
                        val themeScheme = remember(themeId) { KeyboardColorScheme.getTheme(themeId) }

                        Surface(
                            onClick = {
                                app.preferenceManager.updatePreferences { it.copy(themeId = themeId) }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = themeScheme.keyboardBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("theme_card_${themeId.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(themeScheme.actionKeyBackground)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = themeId.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = themeScheme.primaryText
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(16.dp))

        // Typing & Feedback Settings
        Text(
            text = "Feedback & Typing Controls:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Sound
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Keypress Sound", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Audible click when tapping keys", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Switch(
                        checked = preferences.soundEnabled,
                        onCheckedChange = { isChecked ->
                            app.preferenceManager.updatePreferences { it.copy(soundEnabled = isChecked) }
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFF1E293B))

                // Vibration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Haptic Vibration", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Tactile response on keypress", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Switch(
                        checked = preferences.vibrationEnabled,
                        onCheckedChange = { isChecked ->
                            app.preferenceManager.updatePreferences { it.copy(vibrationEnabled = isChecked) }
                        }
                    )
                }

                if (preferences.vibrationEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Vibration Strength: ${preferences.hapticStrength}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Slider(
                        value = preferences.hapticStrength.toFloat(),
                        onValueChange = { value ->
                            app.preferenceManager.updatePreferences { it.copy(hapticStrength = value.toInt()) }
                        },
                        valueRange = 5f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF38BDF8)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFF1E293B))

                // Number Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Number Row", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Display numbers row on top", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Switch(
                        checked = preferences.showNumberRow,
                        onCheckedChange = { isChecked ->
                            app.preferenceManager.updatePreferences { it.copy(showNumberRow = isChecked) }
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFF1E293B))

                // AI Quick Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AI Quick Action Bar", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Floating Gemini shortcut chips above keys", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Switch(
                        checked = preferences.aiQuickBarEnabled,
                        onCheckedChange = { isChecked ->
                            app.preferenceManager.updatePreferences { it.copy(aiQuickBarEnabled = isChecked) }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(16.dp))

        // API Key Settings
        Text(
            text = "Gemini API Configuration:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(6.dp))

        var customKeyInput by remember { mutableStateOf(preferences.customApiKey) }

        OutlinedTextField(
            value = customKeyInput,
            onValueChange = { customKeyInput = it },
            placeholder = { Text("Enter optional custom Gemini API Key...", color = Color(0xFF64748B), fontSize = 12.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedContainerColor = Color(0xFF131C2E),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedTextColor = Color(0xFFF8FAFC),
                unfocusedTextColor = Color(0xFFF8FAFC)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                app.preferenceManager.updatePreferences { it.copy(customApiKey = customKeyInput.trim()) }
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
        ) {
            Text("Save API Key", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActivationTab(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Keyboard Activation & Setup",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Follow these simple steps to enable AI Keyboard on your Android device",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Step 1 Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Enable AI Keyboard in Settings",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Open Android Input Method settings and toggle on 'AI Keyboard'.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_open_ime_settings")
                ) {
                    Text("Open Keyboard Settings", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 2 Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF818CF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Switch Active Input Method",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select 'AI Keyboard' as your default active keyboard for all apps.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        try {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        } catch (_: Exception) {}
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF818CF8)),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_select_ime_picker")
                ) {
                    Text("Select Active Keyboard", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 3 Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Test in any app or Playground",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can type in WhatsApp, Telegram, Gmail, Chrome, or test right here in the Playground tab!",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
