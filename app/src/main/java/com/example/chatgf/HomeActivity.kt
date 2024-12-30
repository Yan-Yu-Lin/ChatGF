package com.example.chatgf

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnNewChat = findViewById<Button>(R.id.btnNewChat)
        val btnContinue = findViewById<Button>(R.id.btnContinueChat)

        btnNewChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        btnContinue.setOnClickListener {
            val chatHistoryManager = ChatHistoryManager(this)
            val histories = chatHistoryManager.getAllChats()

            if (histories.isEmpty()) {
                Toast.makeText(this, "沒有紀錄，請開始新對話", Toast.LENGTH_SHORT).show()
            } else {
                // 顯示歷史對話列表
                showHistoryDialog(histories)
            }
        }
    }

    private fun showHistoryDialog(histories: List<ChatHistory>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.fragment_chat_history)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvChatHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ChatHistoryAdapter(
            histories = histories,
            onHistoryClick = { history ->
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(EXTRA_CHAT_ID, history.id)
                startActivity(intent)
                dialog.dismiss()
            },
            onDeleteClick = { history ->
                ChatHistoryManager(this).deleteChat(history.id)
                // 重新載入列表
                dialog.dismiss()
                showHistoryDialog(ChatHistoryManager(this).getAllChats())
            }
        )

        recyclerView.adapter = adapter
        dialog.show()
    }
}
