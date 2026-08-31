# Testing & Verification Guide

AI Keyboard includes unit tests covering core business logic, text extraction safety, and keyboard layout models.

---

## 1. Running Unit Tests

Run local JVM unit tests using:

```bash
gradle :app:testDebugUnitTest
```

---

## 2. Test Coverage

- **`TextContextExtractorTest`**: Verifies password field detection and safe surrounding text boundaries.
- **`AIProviderTest`**: Tests request construction, local fallback behavior, and response normalization.
- **`KeyboardLayoutsTest`**: Validates key mappings for QWERTY, Arabic RTL, Hausa characters, numbers, and symbols.
