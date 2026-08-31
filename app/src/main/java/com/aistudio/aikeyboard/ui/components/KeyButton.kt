package com.aistudio.aikeyboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.aikeyboard.data.model.KeyboardColorScheme
import com.aistudio.aikeyboard.keyboard.KeyAction
import com.aistudio.aikeyboard.keyboard.KeyModel

@Composable
fun KeyButton(
    keyModel: KeyModel,
    colorScheme: KeyboardColorScheme,
    modifier: Modifier = Modifier,
    onKeyPress: (KeyAction) -> Unit,
    onKeyLongPress: ((KeyAction) -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(durationMillis = 80),
        label = "keyScale"
    )

    val bgColor = when {
        keyModel.isAccent -> if (isPressed) colorScheme.actionKeyPressedBackground else colorScheme.actionKeyBackground
        keyModel.isFunctional -> if (isPressed) colorScheme.keyPressedBackground else colorScheme.keyBackground.copy(alpha = 0.85f)
        else -> if (isPressed) colorScheme.keyPressedBackground else colorScheme.keyBackground
    }

    val textColor = when {
        keyModel.isAccent -> Color.White
        else -> colorScheme.primaryText
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.5.dp, vertical = 3.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .shadow(if (isPressed) 0.dp else 1.dp, RoundedCornerShape(8.dp))
            .pointerInput(keyModel) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onKeyPress(keyModel.action)
                    },
                    onLongPress = {
                        if (onKeyLongPress != null) {
                            onKeyLongPress(keyModel.action)
                        } else if (keyModel.secondaryLabel.isNotEmpty()) {
                            onKeyPress(KeyAction.Text(keyModel.secondaryLabel))
                        }
                    }
                )
            }
            .testTag("key_${keyModel.primaryLabel}"),
        contentAlignment = Alignment.Center
    ) {
        if (keyModel.secondaryLabel.isNotEmpty() && !keyModel.isFunctional) {
            Text(
                text = keyModel.secondaryLabel,
                color = colorScheme.secondaryText.copy(alpha = 0.7f),
                fontSize = 9.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 2.dp)
            )
        }

        Text(
            text = keyModel.primaryLabel,
            color = textColor,
            fontSize = when {
                keyModel.primaryLabel.length > 2 -> 13.sp
                keyModel.primaryLabel.length == 2 -> 15.sp
                else -> 19.sp
            },
            fontWeight = if (keyModel.isFunctional || keyModel.isAccent) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
