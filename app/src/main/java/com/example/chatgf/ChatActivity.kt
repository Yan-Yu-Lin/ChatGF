package com.example.chatgf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages: MutableList<Message> = mutableListOf()

    // 這裡初始化你的 manager，傳入 API Key（實務上不要把 key 寫死在程式裡喔）
    private val openAIManager = OpenAIManager("YOUR_API_KEY")

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerView)
        inputEditText = findViewById(R.id.editTextUserInput)
        sendButton = findViewById(R.id.btnSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 按下「送出」按鈕
        sendButton.setOnClickListener {
            val userInput = inputEditText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // 1. 先把使用者訊息加到列表
                messages.add(Message(role = "user", content = userInput))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                inputEditText.setText("")

                // 2. 呼叫 OpenAI API -> 取得 ChatGPT 回覆
                getChatGPTReply(userInput)
            }
        }
    }

    private fun getChatGPTReply(userInput: String) {
        lifecycleScope.launch {
            try {
                // 在協程中呼叫 manager 的掛起函式
                val responseText = openAIManager.getChatResponse(userInput)
                // 更新列表
                messages.add(Message(role = "assistant", content = responseText))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
                // 這裡可以做錯誤提示或重試
                messages.add(Message(role = "assistant", content = "發生錯誤：${e.message}"))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }
}
