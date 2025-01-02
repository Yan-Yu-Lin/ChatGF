package com.example.chatgf

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class loading : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        lottieAnimationView.playAnimation() // 播放加載動畫

        Handler(Looper.getMainLooper()).postDelayed({
            val targetActivity = intent.getStringExtra("target_activity")
            val chatId = intent.getStringExtra("chat_id")

            when (targetActivity) {
                "newConversation" -> navigateToNewConversation()
                "continueConversation" -> navigateToContinueConversation(chatId)
                else -> {
                    Toast.makeText(this, "未知的目標頁面", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        }, 1000) // 2秒後跳轉
    }

    private fun navigateToNewConversation() {
        // Pass along girlfriend type (if any)
        val gfTypeName = intent.getStringExtra("EXTRA_GIRLFRIEND_TYPE")

        val chatIntent = Intent(this, ChatActivity::class.java)
        gfTypeName?.let {
            chatIntent.putExtra("EXTRA_GIRLFRIEND_TYPE", it)
        }
        startActivity(chatIntent)
        finish()
    }

    private fun navigateToContinueConversation(chatId: String?) {
        if (chatId != null) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(EXTRA_CHAT_ID, chatId)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "無法找到歷史對話", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
