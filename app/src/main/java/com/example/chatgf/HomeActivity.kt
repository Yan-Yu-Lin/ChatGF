package com.example.chatgf

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnNewChat = findViewById<Button>(R.id.btnNewChat)
        val btnContinue = findViewById<Button>(R.id.btnContinueChat)

        // 「開始新對話」
        btnNewChat.setOnClickListener {
            showGirlfriendSelectionDialog()
        }

        // 「繼續對話」
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

    /** 顯示選擇女友的對話框 */
    private fun showGirlfriendSelectionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_girlfriend_selection)

        val gfTypes = listOf(GirlfriendType.GIRL_1, GirlfriendType.GIRL_2, GirlfriendType.GIRL_3, GirlfriendType.RANDOM)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvGirlfriends)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GirlfriendSelectionAdapter(gfTypes) { selectedType ->
            val chosenType = if (selectedType == GirlfriendType.RANDOM) {
                GirlfriendType.pickRandomGirlfriend()
            } else {
                selectedType
            }
            // Different handling for HomeActivity
            val loadingIntent = Intent(this, loading::class.java)
            loadingIntent.putExtra("target_activity", "newConversation")
            loadingIntent.putExtra("EXTRA_GIRLFRIEND_TYPE", chosenType.name)
            startActivity(loadingIntent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showHistoryDialog(histories: List<ChatHistory>) {
        try {
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
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error showing history dialog: ${e.message}")
            Toast.makeText(this, "顯示歷史記錄時出錯", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingAnimation(targetActivity: String, chatId: String? = null) {
        val loadingIntent = Intent(this, loading::class.java)
        loadingIntent.putExtra("target_activity", targetActivity)
        loadingIntent.putExtra("chat_id", chatId)
        startActivity(loadingIntent)
    }
}
