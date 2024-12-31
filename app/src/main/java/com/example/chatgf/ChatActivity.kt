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

// 讀取歷史記錄時使用的 key
public val EXTRA_CHAT_ID = "chat_id"

// ChatActivity：
// 1) 若從 HomeActivity 選了女友類型 -> systemPrompt 會被記錄在一個獨立變數裡 (systemPrompt)。
// 2) systemPrompt 不會顯示在 UI，只在呼叫 API 時帶入（作為第一筆 ChatMessage(role=System)）。
class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<Message> = mutableListOf()

    // 從 BuildConfig 取出 API_KEY
    private val openAIManager = OpenAIManager(BuildConfig.API_KEY)

    private lateinit var chatHistoryManager: ChatHistoryManager
    private var currentChatId: String? = null

    // 用來保存女友類型的 system 提示
    private var systemPrompt: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 取得 UI 元件
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val inputEditText = findViewById<EditText>(R.id.editTextUserInput)
        val sendButton = findViewById<Button>(R.id.btnSend)

        // 建立 Adapter，連接 messages
        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 建立 HistoryManager (存取 SharedPreferences)
        chatHistoryManager = ChatHistoryManager(this)

        // 檢查是否帶了舊的 chatId -> 若有，載入歷史訊息
        intent.getStringExtra(EXTRA_CHAT_ID)?.let { chatId ->
            currentChatId = chatId
            val history = chatHistoryManager.getChatById(chatId)
            if (history != null) {
                messages.addAll(history.messages)
                adapter.notifyDataSetChanged()
            }
        }

        // 檢查是否帶了女友類型 (EXTRA_GIRLFRIEND_TYPE) -> 不放到 messages，僅存於 systemPrompt
        val gfTypeName = intent.getStringExtra("EXTRA_GIRLFRIEND_TYPE")
        if (gfTypeName != null) {
            try {
                val chosenType = GirlfriendType.valueOf(gfTypeName)
                // 如果是 RANDOM，就再 pickRandomGirlfriend()
                val finalType = if (chosenType == GirlfriendType.RANDOM) {
                    GirlfriendType.pickRandomGirlfriend()
                } else {
                    chosenType
                }
                // 設定 systemPrompt (但不顯示在 UI)
                systemPrompt = finalType.systemPrompt

            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                // 若 enum 轉換失敗就無事發生
            }
        }

        // 送出按鈕
        sendButton.setOnClickListener {
            val userInput = inputEditText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // 把使用者輸入加入 UI 列表
                messages.add(Message(role = "user", content = userInput))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                // 清空輸入框
                inputEditText.setText("")

                // 呼叫 OpenAI
                getChatGPTReply()
            }
        }
    }

    /**
     * 依據目前的 messages，外加 systemPrompt，呼叫 OpenAI API。
     * 回覆後添加到 messages (assistant role)。
     */
    private fun getChatGPTReply() {
        lifecycleScope.launch {
            try {
                // 把 systemPrompt + user/assistant messages 一起轉成 OpenAI 的 ChatMessage
                val openAiMessages = buildOpenAIMessages()
                val responseText = openAIManager.getChatResponse(openAiMessages)

                // 將 AI 回覆加入 UI
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
     * 將 systemPrompt (若有) + messages (user/assistant) 拼成 List<ChatMessage> 給 GPT
     */
    private fun buildOpenAIMessages(): List<ChatMessage> {
        val chatMessages = mutableListOf<ChatMessage>()
        // 若 systemPrompt 不為空，就放在最前面
        systemPrompt?.let { prompt ->
            chatMessages.add(ChatMessage(role = ChatRole.System, content = prompt))
        }

        // 接著處理 user & assistant 訊息
        for (msg in messages) {
            val role = when (msg.role) {
                "assistant" -> ChatRole.Assistant
                else -> ChatRole.User // 預設將 "user" or 其他都當作 user
            }
            chatMessages.add(ChatMessage(role = role, content = msg.content))
        }
        return chatMessages
    }

    /**
     * 離開頁面時，若有訊息則保存
     */
    override fun onPause() {
        super.onPause()
        if (messages.isNotEmpty()) {
            chatHistoryManager.saveChat(messages, currentChatId)
        }
    }
}
