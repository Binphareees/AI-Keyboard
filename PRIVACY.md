# Privacy & Security Model

AI Keyboard is built with user privacy and data security as core design principles.

---

## 1. Zero Keystroke Logging
- **No Background Logging**: AI Keyboard does not log, record, or track individual keypresses.
- **No Remote Telemetry**: Key events are committed directly to Android's `InputConnection` and immediately discarded from memory.

## 2. Safe Text Context Extraction
- **Explicit User Action Only**: Text is never analyzed or transmitted in the background. AI processing occurs only when the user explicitly taps an AI action button (e.g., *Fix Grammar*, *Rewrite*, or *Translate*).
- **Strict Password Protection**: When `EditorInfo.inputType` indicates a password field (`TYPE_TEXT_VARIATION_PASSWORD`, `TYPE_TEXT_VARIATION_VISIBLE_PASSWORD`, `TYPE_TEXT_VARIATION_WEB_PASSWORD`, `TYPE_NUMBER_VARIATION_PASSWORD`), AI processing is automatically disabled.

## 3. Secure Credential Storage
- User API keys are stored in private SharedPreferences (`Context.MODE_PRIVATE`).
- Keys are never logged to Logcat or exposed outside the application sandbox.

## 4. Offline First
- When offline or when no API key is provided, the keyboard uses a local heuristic engine for basic text operations and functions as a standard offline keyboard.
