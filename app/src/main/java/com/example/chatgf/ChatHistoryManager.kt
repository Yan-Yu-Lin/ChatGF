package com.example.chatgf

import android.content.Context
import com.google.gson.Gson


class ChatHistoryManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("chat_histories", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveChat(messages: List<Message>) {
        if (messages.isEmpty()) return

        val history = ChatHistory(
            title = messages.firstOrNull { it.role == "user" }?.content?.take(20) ?: "New Chat",
            lastMessage = messages.last().content.take(50),
            timestamp = System.currentTimeMillis(),
            messages = messages
        )

        val historyJson = gson.toJson(history)
        sharedPreferences.edit().putString(history.id, historyJson).apply()
    }

    fun getAllChats(): List<ChatHistory> {
        return sharedPreferences.all.map { entry ->
            gson.fromJson(entry.value as String, ChatHistory::class.java)
        }.sortedByDescending { it.timestamp }
    }

    fun getChatById(id: String): ChatHistory? {
        val historyJson = sharedPreferences.getString(id, null)
        return historyJson?.let { gson.fromJson(it, ChatHistory::class.java) }
    }

    fun deleteChat(id: String) {
        sharedPreferences.edit().remove(id).apply()
    }
}