package com.example.chatgf

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val btnNewChat = findViewById<Button>(R.id.btnNewChat)
        val btnContinue = findViewById<Button>(R.id.btnContinueChat)


        btnNewChat.setOnClickListener {
            showLoadingAnimation("newConversation")
        }

        btnContinue.setOnClickListener {
            val chatHistoryManager = ChatHistoryManager(this)
            val histories = chatHistoryManager.getAllChats()

            if (histories.isEmpty()) {
                Toast.makeText(this, "沒有紀錄，請開始新對話", Toast.LENGTH_SHORT).show()
            } else {
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
                showLoadingAnimation("continueConversation", history.id)
            },
            onDeleteClick = { history ->
                ChatHistoryManager(this).deleteChat(history.id)
                dialog.dismiss()
                showHistoryDialog(ChatHistoryManager(this).getAllChats())
            }
        )

        recyclerView.adapter = adapter
        dialog.show()
    }

    private fun showLoadingAnimation(targetActivity: String, chatId: String? = null) {
        val loadingIntent = Intent(this, loading::class.java)
        loadingIntent.putExtra("target_activity", targetActivity)
        loadingIntent.putExtra("chat_id", chatId)
        startActivity(loadingIntent)
    }
}
