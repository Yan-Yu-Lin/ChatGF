package com.example.chatgf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.DividerItemDecoration
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

public val EXTRA_CHAT_ID = "chat_id"

class ChatActivity : AppCompatActivity() {
    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<Message> = mutableListOf()
    private lateinit var chatHistoryManager: ChatHistoryManager
    private var currentChatId: String? = null
    private lateinit var drawerLayout: DrawerLayout

    private val openAIManager = OpenAIManager(BuildConfig.API_KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        Log.d("ChatActivity", "ChatActivity started")

        // 1. 優先初始化 chatHistoryManager
        chatHistoryManager = ChatHistoryManager(this)

        // 2. 設置側邊欄（現在 chatHistoryManager 已經初始化了）
        setupDrawer()

        // 3. 設置聊天 UI
        setupChatUI()

        // 4. 加載當前聊天記錄
        loadCurrentChat()

        // 5. 更新歷史記錄列表
        updateHistoryList()
    }

    private fun setupDrawer() {
        try {
            drawerLayout = findViewById(R.id.drawerLayout)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            // 允許通過漢堡按鈕開啟選單
            toolbar.setNavigationOnClickListener {
                updateHistoryList()
                drawerLayout.openDrawer(GravityCompat.START)
            }

            // 設置側邊欄的新對話按鈕
            val btnNewChat = findViewById<Button>(R.id.btnNewChatInDrawer)
            btnNewChat.setOnClickListener {
                showGirlfriendSelectionDialog()
            }

            // 設置歷史記錄列表
            val rvHistory = findViewById<RecyclerView>(R.id.rvChatHistoryInDrawer)
            rvHistory.layoutManager = LinearLayoutManager(this)
            rvHistory.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        } catch (e: Exception) {
            Log.e("ChatActivity", "Error in setupDrawer: ${e.message}")
            Toast.makeText(this, "設置側邊欄時出錯", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showGirlfriendSelectionDialog() {
        val gfTypes = listOf(
            GirlfriendType.GIRL_1,
            GirlfriendType.GIRL_2,
            GirlfriendType.GIRL_3,
            GirlfriendType.RANDOM
        )
        val items = gfTypes.map { it.displayName }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("選擇你的女友")
            .setItems(items) { _, which ->
                val chosenType = if (which == 3) {
                    GirlfriendType.pickRandomGirlfriend()
                } else {
                    gfTypes[which]
                }
                startNewChat(chosenType)
            }
            .show()
    }

    private fun updateHistoryList() {
        try {
            val histories = chatHistoryManager.getAllChats()
            val rvHistory = findViewById<RecyclerView>(R.id.rvChatHistoryInDrawer)

            val historyAdapter = ChatHistoryAdapter(
                histories = histories,
                onHistoryClick = { history ->
                    switchToChat(history.id)
                    drawerLayout.closeDrawer(GravityCompat.START)
                },
                onDeleteClick = { history ->
                    chatHistoryManager.deleteChat(history.id)
                    updateHistoryList()
                    if (history.id == currentChatId) {
                        startNewChat()
                    }
                },
                isDrawer = true  // 使用合併後的 Adapter，標記為 drawer 版本
            )

            rvHistory.adapter = historyAdapter
        } catch (e: Exception) {
            Log.e("ChatActivity", "Error updating history list: ${e.message}")
            Toast.makeText(this, "更新歷史記錄時出錯", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChatUI() {
        try {
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val inputEditText = findViewById<EditText>(R.id.editTextUserInput)
            val sendButton = findViewById<Button>(R.id.btnSend)

            adapter = ChatAdapter(messages)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

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
        } catch (e: Exception) {
            Log.e("ChatActivity", "Error in setupChatUI: ${e.message}")
            Toast.makeText(this, "設置聊天界面時出錯", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startNewChat(girlfriendType: GirlfriendType? = null) {
        // 保存當前對話
        if (messages.isNotEmpty()) {
            chatHistoryManager.saveChat(messages, currentChatId)
        }

        // 清空當前對話
        messages.clear()
        adapter.notifyDataSetChanged()
        currentChatId = null
        drawerLayout.closeDrawer(GravityCompat.START)

        girlfriendType?.let {
            messages.add(Message(role = "system", content = it.systemPrompt))
            adapter.notifyDataSetChanged()
        }
    }

    private fun switchToChat(chatId: String) {
        // 保存當前對話
        if (messages.isNotEmpty()) {
            chatHistoryManager.saveChat(messages, currentChatId)
        }

        // 載入選擇的對話
        currentChatId = chatId
        messages.clear()
        chatHistoryManager.getChatById(chatId)?.let { history ->
            messages.addAll(history.messages)
        }
        adapter.notifyDataSetChanged()
    }

    private fun loadCurrentChat() {
        // 從 intent 獲取對話 ID（如果有的話）
        intent.getStringExtra(EXTRA_CHAT_ID)?.let { chatId ->
            currentChatId = chatId
            chatHistoryManager.getChatById(chatId)?.let { history ->
                messages.addAll(history.messages)
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * 核心：把「完整 messages」轉成 OpenAI ChatMessage 後，送到 API 做多輪對話。
     */

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
