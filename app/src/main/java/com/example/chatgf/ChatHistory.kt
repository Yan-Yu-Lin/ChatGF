package com.example.chatgf
import java.util.UUID

data class ChatHistory(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val messages: List<Message>
)
