package com.aistudio.aikeyboard

import android.view.inputmethod.EditorInfo
import com.aistudio.aikeyboard.keyboard.TextContextExtractor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextContextExtractorTest {

    @Test
    fun testPasswordDetection() {
        val editorInfo = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue(TextContextExtractor.isPasswordField(editorInfo))
        assertFalse(TextContextExtractor.isSafeForAi(editorInfo))
    }

    @Test
    fun testNormalTextFieldIsSafe() {
        val editorInfo = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
        }
        assertFalse(TextContextExtractor.isPasswordField(editorInfo))
        assertTrue(TextContextExtractor.isSafeForAi(editorInfo))
    }

    @Test
    fun testNumericPasswordDetection() {
        val editorInfo = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue(TextContextExtractor.isPasswordField(editorInfo))
        assertFalse(TextContextExtractor.isSafeForAi(editorInfo))
    }

    @Test
    fun testWebPasswordDetection() {
        val editorInfo = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
        }
        assertTrue(TextContextExtractor.isPasswordField(editorInfo))
        assertFalse(TextContextExtractor.isSafeForAi(editorInfo))
    }

    @Test
    fun testNoPersonalizedLearningFlag() {
        val editorInfo = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
            imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }
        assertFalse(TextContextExtractor.isPasswordField(editorInfo))
        assertFalse(TextContextExtractor.isSafeForAi(editorInfo))
    }

    @Test
    fun testTypeNullField() {
        val editorInfo = EditorInfo().apply {
            inputType = EditorInfo.TYPE_NULL
        }
        assertFalse(TextContextExtractor.isSafeForAi(editorInfo))
    }
}
