package com.example.chatgf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import kotlinx.coroutines.launch

public val EXTRA_CHAT_ID = "chat_id"

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<Message> = mutableListOf()

    private val openAIManager = OpenAIManager(BuildConfig.API_KEY)
    private lateinit var chatHistoryManager: ChatHistoryManager

    private var currentChatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val inputEditText = findViewById<EditText>(R.id.editTextUserInput)
        val sendButton = findViewById<Button>(R.id.btnSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        chatHistoryManager = ChatHistoryManager(this)

        // 1) Check if continuing an existing chat
        val chatId = intent.getStringExtra(EXTRA_CHAT_ID)
        if (chatId != null) {
            currentChatId = chatId
            val history = chatHistoryManager.getChatById(chatId)
            if (history != null) {
                messages.addAll(history.messages)
                adapter.notifyDataSetChanged()
            }
        }

        // 2) If there's no chatId => new conversation
        // Check if we have a gfTypeName
        if (chatId == null) {
            val gfTypeName = intent.getStringExtra("EXTRA_GIRLFRIEND_TYPE")
            if (gfTypeName != null) {
                try {
                    val chosenType = GirlfriendType.valueOf(gfTypeName)
                    val finalType = if (chosenType == GirlfriendType.RANDOM) {
                        GirlfriendType.pickRandomGirlfriend()
                    } else {
                        chosenType
                    }
                    // Insert system message into messages so it gets stored & loaded next time
                    messages.add(0, Message(role = "system", content = finalType.systemPrompt))
                    // No adapter notify needed if it's the first message, but let's do it anyway:
                    adapter.notifyItemInserted(0)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    // If we fail to parse the gf type, do nothing
                }
            }
        }

        // Send button
        sendButton.setOnClickListener {
            val userInput = inputEditText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                messages.add(Message(role = "user", content = userInput))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                inputEditText.setText("")
                getChatGPTReply()
            }
        }
    }

    private fun getChatGPTReply() {
        lifecycleScope.launch {
            try {
                val openAiMessages = buildOpenAIMessages()
                val responseText = openAIManager.getChatResponse(openAiMessages)

                messages.add(Message(role = "assistant", content = responseText))
                adapter.notifyItemInserted(messages.size - 1)

            } catch (e: Exception) {
                e.printStackTrace()
                messages.add(Message(role = "assistant", content = "Error: ${e.message}"))
                adapter.notifyItemInserted(messages.size - 1)
            }
        }
    }

    /**
     * Convert all messages (including system!) to OpenAI's ChatMessage
     */
    private fun buildOpenAIMessages(): List<ChatMessage> {
        return messages.map { msg ->
            val chatRole = when (msg.role) {
                "system" -> ChatRole.System
                "assistant" -> ChatRole.Assistant
                else -> ChatRole.User
            }
            ChatMessage(role = chatRole, content = msg.content)
        }
    }

    override fun onPause() {
        super.onPause()
        if (messages.isNotEmpty()) {
            chatHistoryManager.saveChat(messages, currentChatId)
        }
    }
}
