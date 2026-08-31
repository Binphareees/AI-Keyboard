package com.aistudio.aikeyboard

import com.aistudio.aikeyboard.data.model.KeyboardLanguage
import com.aistudio.aikeyboard.keyboard.KeyAction
import com.aistudio.aikeyboard.keyboard.KeyboardLayouts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardLayoutsTest {

    @Test
    fun testQwertyLayoutGeneratesCorrectRows() {
        val rows = KeyboardLayouts.getAlphabetRows(
            language = KeyboardLanguage.ENGLISH,
            isShifted = false,
            isCapsLock = false,
            showNumberRow = true
        )
        // With number row: 1 num row + 3 letter rows + 1 bottom row = 5 rows
        assertEquals(5, rows.size)
        // Top row should have 10 numbers
        assertEquals(10, rows[0].size)
        assertEquals("1", rows[0][0].primaryLabel)
    }

    @Test
    fun testHausaLayoutContainsSpecialHookedCharacters() {
        val rows = KeyboardLayouts.getAlphabetRows(
            language = KeyboardLanguage.HAUSA,
            isShifted = false,
            isCapsLock = false,
            showNumberRow = false
        )
        val allKeys = rows.flatten()
        val allLabels = allKeys.map { it.primaryLabel }

        assertTrue(allLabels.contains("ɓ"))
        assertTrue(allLabels.contains("ɗ"))
        assertTrue(allLabels.contains("ƙ"))
        assertTrue(allLabels.contains("ƴ"))
    }

    @Test
    fun testShiftUppercaseTransformation() {
        val rows = KeyboardLayouts.getAlphabetRows(
            language = KeyboardLanguage.ENGLISH,
            isShifted = true,
            isCapsLock = false,
            showNumberRow = false
        )
        val allKeys = rows.flatten()
        val textKeys = allKeys.filter { it.action is KeyAction.Text }

        // All standard letters should be uppercase
        assertTrue(textKeys.any { it.primaryLabel == "Q" })
        assertTrue(textKeys.none { it.primaryLabel == "q" })
    }

    @Test
    fun testSymbolsRowsGeneration() {
        val symbolsRows = KeyboardLayouts.getSymbolsRows()
        assertEquals(4, symbolsRows.size) // 3 symbol rows + 1 bottom row
    }

    @Test
    fun testArabicLayoutGeneratesArabicCharacters() {
        val rows = KeyboardLayouts.getAlphabetRows(
            language = KeyboardLanguage.ARABIC,
            isShifted = false,
            isCapsLock = false,
            showNumberRow = false
        )
        val allKeys = rows.flatten()
        val allLabels = allKeys.map { it.primaryLabel }

        // Verify representative Arabic alphabet keys
        assertTrue(allLabels.contains("ض"))
        assertTrue(allLabels.contains("ص"))
        assertTrue(allLabels.contains("ث"))
        assertTrue(allLabels.contains("ق"))
        assertTrue(allLabels.contains("ف"))
        assertTrue(allLabels.contains("غ"))
        assertTrue(allLabels.contains("ع"))
    }
}
