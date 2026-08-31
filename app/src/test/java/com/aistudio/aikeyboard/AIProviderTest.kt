package com.aistudio.aikeyboard

import com.aistudio.aikeyboard.ai.provider.AIRequest
import com.aistudio.aikeyboard.ai.provider.LocalFallbackProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AIProviderTest {

    private val localProvider = LocalFallbackProvider()

    @Test
    fun testLocalFallbackGrammarFix() = runBlocking {
        val request = AIRequest(
            prompt = "i want to test this keyboard",
            systemInstruction = "fix grammar and capitalization"
        )
        val response = localProvider.generateText(request)
        assertTrue(response.isSuccess)
        assertTrue(response.text.startsWith("I"))
        assertTrue(response.text.endsWith("."))
    }

    @Test
    fun testLocalFallbackProfessionalTone() = runBlocking {
        val request = AIRequest(
            prompt = "please send the files",
            systemInstruction = "make professional"
        )
        val response = localProvider.generateText(request)
        assertTrue(response.isSuccess)
        assertTrue(response.text.contains("Dear recipient"))
    }

    @Test
    fun testLocalFallbackCasualTone() = runBlocking {
        val request = AIRequest(
            prompt = "Please send the files",
            systemInstruction = "make casual"
        )
        val response = localProvider.generateText(request)
        assertTrue(response.isSuccess)
        assertTrue(response.text.contains("Hey!"))
    }

    @Test
    fun testLocalFallbackShorten() = runBlocking {
        val request = AIRequest(
            prompt = "This is the first sentence. This is the second sentence that is too long.",
            systemInstruction = "make concise and short"
        )
        val response = localProvider.generateText(request)
        assertTrue(response.isSuccess)
        assertEquals("This is the first sentence.", response.text)
    }

    @Test
    fun testLocalFallbackSummarize() = runBlocking {
        val request = AIRequest(
            prompt = "Line one\nLine two",
            systemInstruction = "summarize into bullet points"
        )
        val response = localProvider.generateText(request)
        assertTrue(response.isSuccess)
        assertTrue(response.text.startsWith("• Line one"))
    }

    @Test
    fun testLocalFallbackEmojify() = runBlocking {
        val request = AIRequest(
            prompt = "Great work team",
            systemInstruction = "add emojis"
        )
        val response = localProvider.generateText(request)
        assertTrue(response.isSuccess)
        assertTrue(response.text.contains("✨🚀🎯"))
    }
}
