package com.aistudio.aikeyboard.keyboard

import com.aistudio.aikeyboard.data.model.KeyboardLanguage

object KeyboardLayouts {

    fun getAlphabetRows(
        language: KeyboardLanguage,
        isShifted: Boolean,
        isCapsLock: Boolean,
        showNumberRow: Boolean
    ): List<List<KeyModel>> {
        val rows = mutableListOf<List<KeyModel>>()

        // Optional Top Number Row
        if (showNumberRow) {
            val numRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map {
                KeyModel(
                    primaryLabel = it,
                    action = KeyAction.Text(it),
                    weight = 1.0f
                )
            }
            rows.add(numRow)
        }

        val uppercase = isShifted || isCapsLock

        when (language) {
            KeyboardLanguage.ARABIC -> {
                // Arabic layout with native character distribution
                val r1Chars = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "د")
                val r2Chars = listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط")
                val r3Chars = listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ")

                rows.add(r1Chars.map {
                    KeyModel(primaryLabel = it, action = KeyAction.Text(it))
                })
                rows.add(r2Chars.map {
                    KeyModel(primaryLabel = it, action = KeyAction.Text(it))
                })

                val row3 = mutableListOf<KeyModel>()
                row3.add(KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.3f, isFunctional = true))
                row3.addAll(r3Chars.map {
                    KeyModel(primaryLabel = it, action = KeyAction.Text(it))
                })
                row3.add(KeyModel(primaryLabel = "!", action = KeyAction.Text("!"), weight = 0.8f))
                rows.add(row3)
            }
            KeyboardLanguage.HAUSA -> {
                // Hausa layout with ɓ, ɗ, ƙ, ƴ
                val r1Chars = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "ɓ")
                val r2Chars = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ɗ")
                val r3Chars = listOf("z", "x", "c", "v", "b", "n", "m", "ƙ", "ƴ")

                rows.add(r1Chars.map {
                    val char = if (uppercase) it.uppercase() else it.lowercase()
                    KeyModel(primaryLabel = char, action = KeyAction.Text(char))
                })
                rows.add(r2Chars.map {
                    val char = if (uppercase) it.uppercase() else it.lowercase()
                    KeyModel(primaryLabel = char, action = KeyAction.Text(char))
                })

                val row3 = mutableListOf<KeyModel>()
                row3.add(KeyModel(primaryLabel = if (isCapsLock) "⇪" else "⇧", action = KeyAction.Shift, weight = 1.3f, isFunctional = true, isAccent = uppercase))
                row3.addAll(r3Chars.map {
                    val char = if (uppercase) it.uppercase() else it.lowercase()
                    KeyModel(primaryLabel = char, action = KeyAction.Text(char))
                })
                row3.add(KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.3f, isFunctional = true))
                rows.add(row3)
            }
            else -> {
                // Standard QWERTY
                val r1Chars = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
                val r2Chars = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
                val r3Chars = listOf("z", "x", "c", "v", "b", "n", "m")

                val r1Secondary = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
                val r2Secondary = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
                val r3Secondary = listOf("*", "\"", "'", ":", ";", "!", "?")

                rows.add(r1Chars.mapIndexed { idx, it ->
                    val char = if (uppercase) it.uppercase() else it.lowercase()
                    KeyModel(primaryLabel = char, secondaryLabel = if (!showNumberRow) r1Secondary.getOrElse(idx) { "" } else "", action = KeyAction.Text(char))
                })

                rows.add(r2Chars.mapIndexed { idx, it ->
                    val char = if (uppercase) it.uppercase() else it.lowercase()
                    KeyModel(primaryLabel = char, secondaryLabel = r2Secondary.getOrElse(idx) { "" }, action = KeyAction.Text(char))
                })

                val row3 = mutableListOf<KeyModel>()
                row3.add(KeyModel(primaryLabel = if (isCapsLock) "⇪" else "⇧", action = KeyAction.Shift, weight = 1.5f, isFunctional = true, isAccent = uppercase))
                row3.addAll(r3Chars.mapIndexed { idx, it ->
                    val char = if (uppercase) it.uppercase() else it.lowercase()
                    KeyModel(primaryLabel = char, secondaryLabel = r3Secondary.getOrElse(idx) { "" }, action = KeyAction.Text(char))
                })
                row3.add(KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.5f, isFunctional = true))
                rows.add(row3)
            }
        }

        // Bottom Functional Row
        val bottomRow = listOf(
            KeyModel(primaryLabel = "?123", action = KeyAction.SwitchSymbols, weight = 1.4f, isFunctional = true),
            KeyModel(primaryLabel = "🌐", action = KeyAction.LanguageSwitch, weight = 1.0f, isFunctional = true),
            KeyModel(primaryLabel = "AI ✨", action = KeyAction.OpenAiToolbar, weight = 1.3f, isFunctional = true, isAccent = true),
            KeyModel(primaryLabel = "Space", action = KeyAction.Space, weight = 3.6f),
            KeyModel(primaryLabel = "📋", action = KeyAction.OpenClipboard, weight = 1.0f, isFunctional = true),
            KeyModel(primaryLabel = "😊", action = KeyAction.OpenEmoji, weight = 1.0f, isFunctional = true),
            KeyModel(primaryLabel = "↵", action = KeyAction.Enter, weight = 1.5f, isFunctional = true, isAccent = true)
        )
        rows.add(bottomRow)

        return rows
    }

    fun getSymbolsRows(): List<List<KeyModel>> {
        val rows = mutableListOf<List<KeyModel>>()

        val r1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").map {
            KeyModel(primaryLabel = it, action = KeyAction.Text(it))
        }
        val r2 = listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/").map {
            KeyModel(primaryLabel = it, action = KeyAction.Text(it))
        }
        val r3 = listOf("*", "\"", "'", ":", ";", "!", "?").map {
            KeyModel(primaryLabel = it, action = KeyAction.Text(it))
        }

        rows.add(r1)
        rows.add(r2)

        val row3 = mutableListOf<KeyModel>()
        row3.add(KeyModel(primaryLabel = "=/<", action = KeyAction.SwitchMoreSymbols, weight = 1.5f, isFunctional = true))
        row3.addAll(r3)
        row3.add(KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.5f, isFunctional = true))
        rows.add(row3)

        val bottomRow = listOf(
            KeyModel(primaryLabel = "ABC", action = KeyAction.SwitchLetters, weight = 1.4f, isFunctional = true),
            KeyModel(primaryLabel = ",", action = KeyAction.Text(","), weight = 1.0f),
            KeyModel(primaryLabel = "AI ✨", action = KeyAction.OpenAiToolbar, weight = 1.3f, isFunctional = true, isAccent = true),
            KeyModel(primaryLabel = "Space", action = KeyAction.Space, weight = 3.6f),
            KeyModel(primaryLabel = ".", action = KeyAction.Text("."), weight = 1.0f),
            KeyModel(primaryLabel = "😊", action = KeyAction.OpenEmoji, weight = 1.0f, isFunctional = true),
            KeyModel(primaryLabel = "↵", action = KeyAction.Enter, weight = 1.5f, isFunctional = true, isAccent = true)
        )
        rows.add(bottomRow)

        return rows
    }

    fun getMoreSymbolsRows(): List<List<KeyModel>> {
        val rows = mutableListOf<List<KeyModel>>()

        val r1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆").map {
            KeyModel(primaryLabel = it, action = KeyAction.Text(it))
        }
        val r2 = listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\").map {
            KeyModel(primaryLabel = it, action = KeyAction.Text(it))
        }
        val r3 = listOf("%", "©", "®", "™", "✓", "[", "]").map {
            KeyModel(primaryLabel = it, action = KeyAction.Text(it))
        }

        rows.add(r1)
        rows.add(r2)

        val row3 = mutableListOf<KeyModel>()
        row3.add(KeyModel(primaryLabel = "?123", action = KeyAction.SwitchSymbols, weight = 1.5f, isFunctional = true))
        row3.addAll(r3)
        row3.add(KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.5f, isFunctional = true))
        rows.add(row3)

        val bottomRow = listOf(
            KeyModel(primaryLabel = "ABC", action = KeyAction.SwitchLetters, weight = 1.4f, isFunctional = true),
            KeyModel(primaryLabel = "<", action = KeyAction.Text("<"), weight = 1.0f),
            KeyModel(primaryLabel = "AI ✨", action = KeyAction.OpenAiToolbar, weight = 1.3f, isFunctional = true, isAccent = true),
            KeyModel(primaryLabel = "Space", action = KeyAction.Space, weight = 3.6f),
            KeyModel(primaryLabel = ">", action = KeyAction.Text(">"), weight = 1.0f),
            KeyModel(primaryLabel = "😊", action = KeyAction.OpenEmoji, weight = 1.0f, isFunctional = true),
            KeyModel(primaryLabel = "↵", action = KeyAction.Enter, weight = 1.5f, isFunctional = true, isAccent = true)
        )
        rows.add(bottomRow)

        return rows
    }
}
