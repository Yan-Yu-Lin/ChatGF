package com.example.chatgf

data class Message(
    val role: String,   // "system", "user", "assistant"
    val content: String
)
