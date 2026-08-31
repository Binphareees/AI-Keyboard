# AI Providers & Extensibility

AI Keyboard uses a modular provider pattern to support multiple AI backends.

---

## 1. Provider Interface

```kotlin
interface AIProvider {
    val name: String
    val providerType: AIProviderType
    suspend fun generateText(request: AIRequest): AIResponse
    suspend fun isConfigured(): Boolean
}
```

---

## 2. Supported Providers

1. **Google Gemini (Default)**
   - Utilizes Gemini 2.5 Flash / Pro via Google AI REST APIs.
   - High speed, accurate multilingual support, and rich contextual responses.

2. **OpenAI / Anthropic Compatible Endpoints**
   - Supports custom base URLs and headers for OpenAI format (`v1/chat/completions`).

3. **Local Fallback Heuristics**
   - Built-in heuristic rule engine that formats, capitalizes, cleans punctuation, and applies prompt templates locally without network access.

---

## 3. Adding a New Provider

To add a new AI provider:
1. Implement the `AIProvider` interface under `com.aistudio.aikeyboard.ai.provider`.
2. Register the provider in `AIProviderType`.
3. Add the provider selection to the AI Settings screen in `MainScreen.kt`.
