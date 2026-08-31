# AI Keyboard Architecture

AI Keyboard is built on modern Android development standards using Kotlin, Jetpack Compose, Coroutines, StateFlow, Room Database, and the official `android.inputmethodservice.InputMethodService` framework.

---

## Architecture Overview

```
                               ┌─────────────────────────────────────────┐
                               │             Android OS / IME            │
                               └────────────────────┬────────────────────┘
                                                    │
                               ┌────────────────────▼────────────────────┐
                               │             AIKeyboardIME               │
                               │        (InputMethodService)             │
                               └──────┬───────────────────────────┬──────┘
                                      │                           │
          ┌───────────────────────────▼─────┐       ┌─────────────▼─────────────────────────┐
          │      UI & Presentation Layer    │       │        Domain & Business Logic        │
          │  • KeyboardView (ComposeView)   │       │  • KeyboardState / KeyAction          │
          │  • AiQuickToolbar & StudioSheet │       │  • SoundHapticManager                 │
          │  • ClipboardManagerSheet        │       │  • TextContextExtractor (Safe Context)│
          │  • EmojiPicker                  │       │  • AiUseCases (Transform, Translate)  │
          └─────────────────────────────────┘       └─────────────┬─────────────────────────┘
                                                                  │
                               ┌──────────────────────────────────▼─────────────────────────┐
                               │               Pluggable AI Provider Layer                  │
                               │  ┌──────────────────────────────────────────────────────┐  │
                               │  │                 interface AIProvider                 │  │
                               │  └──────────┬───────────────────┬───────────────────┬───┘  │
                               │             │                   │                   │      │
                               │   ┌─────────▼────────┐ ┌────────▼────────┐ ┌────────▼────┐ │
                               │   │  GeminiProvider  │ │ OpenAIProvider  │ │ LocalEngine│ │
                               │   └──────────────────┘ └─────────────────┘ └─────────────┘ │
                               └──────────────────────────────────┬─────────────────────────┘
                                                                  │
                               ┌──────────────────────────────────▼─────────────────────────┐
                               │                   Data & Persistence Layer                 │
                               │  • KeyboardPreferenceManager (Encrypted / DataStore)       │
                               │  • AppDatabase (Room) -> ClipboardDao                      │
                               │  • RetrofitClient (OkHttp, Kotlin Serialization)           │
                               └────────────────────────────────────────────────────────────┘
```

---

## Core Modules

### 1. `service.AIKeyboardIME`
Extends `android.inputmethodservice.InputMethodService`. Manages IME lifecycles, lifecycle registry owners for Compose View trees, and routes key events directly to `InputConnection` (`commitText`, `deleteSurroundingText`, `sendKeyEvent`).

### 2. `keyboard.TextContextExtractor`
Safely inspects editor context (`EditorInfo`). Detects password variations (`TYPE_TEXT_VARIATION_PASSWORD`, `TYPE_NUMBER_VARIATION_PASSWORD`) to block sensitive data extraction.

### 3. `ai.provider`
Defines the `AIProvider` interface and concrete implementations:
- `GeminiProvider`: Google Gemini 2.5 REST API implementation.
- `LocalFallbackProvider`: Zero-latency offline text heuristics ensuring no dead ends.

### 4. `ui.components`
Modular Jetpack Compose components:
- `KeyboardView`: Root keyboard composition supporting responsive key grids.
- `AiQuickToolbar`: Top action bar for one-tap AI shortcuts.
- `AiStudioSheet`: In-keyboard editor for diff reviews and prompt execution.
- `ClipboardManagerSheet`: Persistent clipboard manager.
- `EmojiPicker`: Multi-category emoji selector.
