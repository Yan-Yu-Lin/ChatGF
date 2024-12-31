package com.example.chatgf

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class loading : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        lottieAnimationView.playAnimation()  // 播放加載動畫

        // 假設加載動畫持續 2 秒鐘，之後跳轉到對應頁面
        Handler(Looper.getMainLooper()).postDelayed({
            // 根據目標頁面選擇跳轉
            val targetActivity = intent.getStringExtra("target_activity")
            val chatId = intent.getStringExtra("chat_id") // 獲取對話ID

            when (targetActivity) {
                "newConversation" -> navigateToNewConversation()
                "continueConversation" -> navigateToContinueConversation(chatId)
            }
        }, 5000) // 2秒後跳轉
    }

    private fun navigateToNewConversation() {
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }

    private fun navigateToContinueConversation(chatId: String?) {
        // 確保 chatId 存在
        if (chatId != null) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(EXTRA_CHAT_ID, chatId)  // 傳遞選中的對話ID
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "無法找到歷史對話", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}
