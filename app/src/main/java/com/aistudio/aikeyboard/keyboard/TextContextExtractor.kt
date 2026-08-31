package com.aistudio.aikeyboard.keyboard

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

object TextContextExtractor {

    fun isPasswordField(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        val inputType = editorInfo.inputType
        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION
        val clazz = inputType and EditorInfo.TYPE_MASK_CLASS

        // Explicit password variations across text and number classes
        val isTextPassword = clazz == EditorInfo.TYPE_CLASS_TEXT && (
                variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
        )
        val isNumberPassword = clazz == EditorInfo.TYPE_CLASS_NUMBER && (
                variation == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        )

        return isTextPassword || isNumberPassword
    }

    fun isSafeForAi(editorInfo: EditorInfo?): Boolean {
        if (editorInfo == null) return false
        if (isPasswordField(editorInfo)) return false

        // Check if editor requested no personalized learning or incognito mode
        if ((editorInfo.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING) != 0) {
            return false
        }

        // Check for raw null type (non-interactive or private view)
        if (editorInfo.inputType == EditorInfo.TYPE_NULL) {
            return false
        }

        return true
    }

    fun extractSelectedOrCurrentText(inputConnection: InputConnection?, editorInfo: EditorInfo?): String {
        if (inputConnection == null || !isSafeForAi(editorInfo)) {
            return ""
        }

        // 1. Try to get actively selected text
        val selected = inputConnection.getSelectedText(0)
        if (!selected.isNullOrBlank()) {
            return selected.toString().trim()
        }

        // 2. Otherwise get surrounding text (before cursor + after cursor)
        val before = inputConnection.getTextBeforeCursor(300, 0)?.toString() ?: ""
        val after = inputConnection.getTextAfterCursor(100, 0)?.toString() ?: ""

        val combined = (before + after).trim()
        return combined
    }
}
