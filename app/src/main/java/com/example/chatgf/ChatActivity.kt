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

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<Message> = mutableListOf()

    // 這裡可以先硬編碼 Key，或用更安全的方式存放
    private val openAIManager = OpenAIManager("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val inputEditText = findViewById<EditText>(R.id.editTextUserInput)
        val sendButton = findViewById<Button>(R.id.btnSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // (可選) 加入 System role 引導 AI 的角色或風格
        // messages.add(Message("system", "You are a helpful assistant..."))

        sendButton.setOnClickListener {
            val userInput = inputEditText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // 1. 先把使用者訊息加進 messages
                messages.add(Message(role = "user", content = userInput))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                // 2. 清空輸入框
                inputEditText.setText("")

                // 3. 呼叫 AI
                getChatGPTReply()
            }
        }
    }

    /**
     * 核心：把「完整 messages」轉成 OpenAI ChatMessage 後，送到 API 做多輪對話。
     */
    private fun getChatGPTReply() {
        lifecycleScope.launch {
            try {
                // a) 轉成 openAiMessages (List<ChatMessage>)
                val openAiMessages = convertToOpenAIMessages(messages)

                // b) 呼叫 API 取得回覆
                val responseText = openAIManager.getChatResponse(openAiMessages)

                // c) 將 AI 回覆加入本地對話紀錄 (role = "assistant")
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
     * 將本地的 Message (UI) 轉成 OpenAI 的 ChatMessage。
     * role: user / assistant / system -> ChatRole.User / ChatRole.Assistant / ChatRole.System
     */
    private fun convertToOpenAIMessages(localMessages: List<Message>): List<ChatMessage> {
        return localMessages.map { msg ->
            val chatRole = when (msg.role) {
                "system" -> ChatRole.System
                "assistant" -> ChatRole.Assistant
                "user" -> ChatRole.User
                else -> ChatRole.User
            }
            ChatMessage(role = chatRole, content = msg.content)
        }
    }
}
