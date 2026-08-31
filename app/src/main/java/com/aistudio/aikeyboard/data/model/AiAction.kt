package com.aistudio.aikeyboard.data.model

enum class AiAction(
    val title: String,
    val description: String,
    val systemPrompt: String,
    val iconName: String
) {
    SUMMARIZE(
        title = "Summarize",
        description = "Condense into key points",
        systemPrompt = "Summarize the following text clearly and concisely, highlighting the key takeaways. Do not include markdown code blocks.",
        iconName = "summarize"
    ),
    GRAMMAR_FIX(
        title = "Fix Grammar",
        description = "Correct typos, grammar, and polish",
        systemPrompt = "Fix all grammar, spelling, punctuation, and wording errors in the text. Return only the polished text without commentary or quotation marks.",
        iconName = "spellcheck"
    ),
    SHORTEN(
        title = "Make Concise",
        description = "Trim fluff and make punchy",
        systemPrompt = "Make the following text significantly shorter, more direct, and concise while keeping the essential message. Return only the shortened text.",
        iconName = "compress"
    ),
    LENGTHEN(
        title = "Elaborate",
        description = "Add helpful details and eloquence",
        systemPrompt = "Elaborate on the following text, expanding the thoughts with helpful context, eloquence, and clarity. Return only the elaborated text.",
        iconName = "expand"
    ),
    PROFESSIONAL(
        title = "Professional Tone",
        description = "Rewrite for workplace and business",
        systemPrompt = "Rewrite the following text in a polite, confident, polished, and professional tone suitable for business or formal communication. Return only the rewritten text.",
        iconName = "business"
    ),
    CASUAL(
        title = "Casual & Friendly",
        description = "Relaxed natural chat tone",
        systemPrompt = "Rewrite the following text in a warm, friendly, natural, and conversational tone suitable for messaging friends. Return only the rewritten text.",
        iconName = "chat"
    ),
    TRANSLATE(
        title = "Translate",
        description = "Translate to target language",
        systemPrompt = "Translate the following text accurately into the requested language. Return only the translation.",
        iconName = "translate"
    ),
    SMART_REPLY(
        title = "Smart Reply",
        description = "Draft thoughtful responses",
        systemPrompt = "You are a smart communication assistant. Draft 2-3 short, natural, and helpful reply options to the incoming message or conversation snippet provided. Separate each reply with a newline.",
        iconName = "reply"
    ),
    EMOJIFY(
        title = "Add Emojis",
        description = "Decorate text with emojis",
        systemPrompt = "Add fitting, expressive emojis throughout the following text to make it lively and engaging. Keep the original text intact.",
        iconName = "emoji"
    ),
    CUSTOM_PROMPT(
        title = "Custom Prompt",
        description = "Instruct Gemini AI freely",
        systemPrompt = "Follow the user's specific instruction on the provided text accurately.",
        iconName = "psychology"
    )
}
