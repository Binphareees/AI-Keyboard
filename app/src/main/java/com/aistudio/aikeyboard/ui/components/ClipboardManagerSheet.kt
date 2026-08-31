package com.aistudio.aikeyboard.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.aikeyboard.data.model.ClipboardItem
import com.aistudio.aikeyboard.data.model.KeyboardColorScheme

@Composable
fun ClipboardManagerSheet(
    colorScheme: KeyboardColorScheme,
    items: List<ClipboardItem>,
    onItemClick: (String) -> Unit,
    onTogglePin: (ClipboardItem) -> Unit,
    onDeleteItem: (ClipboardItem) -> Unit,
    onClearUnpinned: () -> Unit,
    onBackToKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items
        else items.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(colorScheme.keyboardBackground)
    ) {
        // Top Header & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.keyBackground)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                onClick = onBackToKeyboard,
                shape = RoundedCornerShape(6.dp),
                color = colorScheme.actionKeyBackground,
                modifier = Modifier.height(32.dp).testTag("btn_clipboard_back_abc")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Text(" ABC", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search clipboard...", fontSize = 11.sp, color = colorScheme.secondaryText) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.accentColor,
                    unfocusedBorderColor = colorScheme.suggestionDividerColor,
                    focusedTextColor = colorScheme.primaryText,
                    unfocusedTextColor = colorScheme.primaryText
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("input_clipboard_search")
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onClearUnpinned,
                modifier = Modifier.size(32.dp).testTag("btn_clipboard_clear_unpinned")
            ) {
                Icon(Icons.Default.ClearAll, contentDescription = "Clear unpinned", tint = colorScheme.secondaryText)
            }
        }

        // List
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No matching clips" else "Clipboard history is empty\nCopy text anywhere to see it here",
                    color = colorScheme.secondaryText,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onItemClick(item.text) }
                            .testTag("clip_item_${item.id}"),
                        color = if (item.isPinned) colorScheme.actionKeyBackground.copy(alpha = 0.15f) else colorScheme.keyBackground,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.text,
                                color = colorScheme.primaryText,
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onTogglePin(item) },
                                    modifier = Modifier.size(24.dp).testTag("pin_btn_${item.id}")
                                ) {
                                    Icon(
                                        if (item.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin",
                                        tint = if (item.isPinned) colorScheme.accentColor else colorScheme.secondaryText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteItem(item) },
                                    modifier = Modifier.size(24.dp).testTag("delete_btn_${item.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = colorScheme.secondaryText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
