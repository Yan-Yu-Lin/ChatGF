package com.example.chatgf

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import kotlinx.coroutines.launch
import java.util.*

public val EXTRA_CHAT_ID = "chat_id"

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<Message> = mutableListOf()

    val API_KEY = BuildConfig.API_KEY
    private val openAIManager = OpenAIManager(API_KEY)

    private lateinit var chatHistoryManager: ChatHistoryManager
    private var currentChatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. 根據季節設置背景圖片
        val season = getSeason()
        val backgroundRes = when (season) {
            "spring" -> R.drawable.spring  // spring.jpg
            "summer" -> R.drawable.summer  // summer.jpg
            "autumn" -> R.drawable.fall    // autumn.jpg
            "winter" -> R.drawable.winter  // winter.jpg
            else -> R.drawable.spring // 默認背景
        }

        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImage)
        backgroundImageView.setImageResource(backgroundRes)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val inputEditText = findViewById<EditText>(R.id.editTextUserInput)
        val sendButton = findViewById<Button>(R.id.btnSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 其他初始化代碼...

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

        chatHistoryManager = ChatHistoryManager(this)

        // 如果是從歷史記錄進來的，載入歷史訊息
        intent.getStringExtra(EXTRA_CHAT_ID)?.let { chatId ->
            currentChatId = chatId // 保存當前對話ID
            chatHistoryManager.getChatById(chatId)?.let { history ->
                messages.addAll(history.messages)
                adapter.notifyDataSetChanged()
            }
        }
    }

    // 根據當前月份判斷季節，並返回季節名稱
    fun getSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1 // 獲取當前月份，注意月份是從0開始的
        return when (month) {
            in 3..5 -> "spring"  // 春天 (3-5月)
            in 6..8 -> "summer"  // 夏天 (6-8月)
            in 9..11 -> "autumn" // 秋天 (9-11月)
            else -> "winter"     // 冬天 (12月、1-2月)
        }
    }

    // 核心：把「完整 messages」轉成 OpenAI ChatMessage 後，送到 API 做多輪對話。
    private fun getChatGPTReply() {
        lifecycleScope.launch {
            try {
                // 轉成 openAiMessages (List<ChatMessage>)
                val openAiMessages = convertToOpenAIMessages(messages)

                // 呼叫 API 取得回覆
                val responseText = openAIManager.getChatResponse(openAiMessages)

                // 將 AI 回覆加入本地對話紀錄 (role = "assistant")
                messages.add(Message(role = "assistant", content = responseText))
                adapter.notifyItemInserted(messages.size - 1)

            } catch (e: Exception) {
                e.printStackTrace()
                messages.add(Message(role = "assistant", content = "Error: ${e.message}"))
                adapter.notifyItemInserted(messages.size - 1)
            }
        }
    }

    // 將本地的 Message (UI) 轉成 OpenAI 的 ChatMessage。
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

    // 在離開活動時儲存對話
    override fun onPause() {
        super.onPause()
        if (messages.isNotEmpty()) {
            chatHistoryManager.saveChat(messages, currentChatId)
        }
    }
}
