package com.aistudio.aikeyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.aikeyboard.ai.AiEngine
import com.aistudio.aikeyboard.data.model.AiAction
import com.aistudio.aikeyboard.data.model.KeyboardColorScheme
import com.aistudio.aikeyboard.data.model.KeyboardLanguage
import kotlinx.coroutines.launch

@Composable
fun AiStudioSheet(
    colorScheme: KeyboardColorScheme,
    initialInput: String,
    onInsertText: (String) -> Unit,
    onBackToKeyboard: () -> Unit,
    customApiKey: String = "",
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf(initialInput) }
    var selectedAction by remember { mutableStateOf(AiAction.GRAMMAR_FIX) }
    var selectedLanguage by remember { mutableStateOf(KeyboardLanguage.ENGLISH) }
    var customPrompt by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun executeAi() {
        if (inputText.isBlank() && selectedAction != AiAction.SMART_REPLY) {
            errorMessage = "Please enter or paste some text first."
            return
        }
        isLoading = true
        errorMessage = null
        resultText = ""

        coroutineScope.launch {
            val result = AiEngine.processText(
                inputText = inputText,
                action = selectedAction,
                targetLanguage = selectedLanguage.displayName,
                customPrompt = customPrompt,
                userApiKey = customApiKey
            )
            isLoading = false
            result.onSuccess { output ->
                resultText = output
            }.onFailure { err ->
                errorMessage = err.message ?: "Failed to generate AI response"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .background(colorScheme.keyboardBackground)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = colorScheme.accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Writing Studio",
                    color = colorScheme.primaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onBackToKeyboard,
                modifier = Modifier.size(28.dp).testTag("btn_close_ai_studio")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = colorScheme.secondaryText)
            }
        }

        // Action selection row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AiAction.entries.forEach { action ->
                val isSelected = action == selectedAction
                Surface(
                    onClick = { selectedAction = action },
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) colorScheme.actionKeyBackground else colorScheme.keyBackground,
                    modifier = Modifier.height(28.dp).testTag("studio_action_${action.name}")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = action.title,
                            color = if (isSelected) Color.White else colorScheme.primaryText,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Translation Language or Custom Prompt input if selected
        if (selectedAction == AiAction.TRANSLATE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To:",
                    color = colorScheme.secondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                KeyboardLanguage.entries.forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    Surface(
                        onClick = { selectedLanguage = lang },
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) colorScheme.accentColor.copy(alpha = 0.25f) else Color.Transparent,
                        modifier = Modifier.height(24.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.displayName,
                                color = if (isSelected) colorScheme.accentColor else colorScheme.secondaryText,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        } else if (selectedAction == AiAction.CUSTOM_PROMPT) {
            OutlinedTextField(
                value = customPrompt,
                onValueChange = { customPrompt = it },
                placeholder = { Text("e.g., Make it sound poetic, add bullet points...", fontSize = 11.sp, color = colorScheme.secondaryText) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.accentColor,
                    unfocusedBorderColor = colorScheme.suggestionDividerColor,
                    focusedTextColor = colorScheme.primaryText,
                    unfocusedTextColor = colorScheme.primaryText
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("input_custom_prompt")
            )
        }

        // Input text box
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Type or paste text to transform...", fontSize = 11.sp, color = colorScheme.secondaryText) },
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.accentColor,
                unfocusedBorderColor = colorScheme.suggestionDividerColor,
                focusedTextColor = colorScheme.primaryText,
                unfocusedTextColor = colorScheme.primaryText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("input_ai_studio_text")
        )

        // Action Trigger Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedAction.description,
                color = colorScheme.secondaryText,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { executeAi() },
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.actionKeyBackground),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("btn_run_ai")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generating...", fontSize = 11.sp, color = Color.White)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Run ${selectedAction.title}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Error message if any
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color(0xFFF43F5E),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Result Card if any
        if (resultText.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                shape = RoundedCornerShape(8.dp),
                color = colorScheme.keyBackground,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "AI Result:",
                        color = colorScheme.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = resultText,
                        color = colorScheme.primaryText,
                        fontSize = 12.sp,
                        modifier = Modifier.testTag("ai_result_content")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onInsertText(resultText)
                                onBackToKeyboard()
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.actionKeyBackground),
                            modifier = Modifier.height(30.dp).testTag("btn_insert_studio_result")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Insert into Text Field", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
