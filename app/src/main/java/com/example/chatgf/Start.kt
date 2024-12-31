package com.example.chatgf

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class Start : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToHome()
        }, 5000) // 延遲 2000 毫秒 (2秒)
    }
        /*        // 取得 Lottie 動畫視圖
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)

        // 設置動畫並播放
        lottieAnimationView.setAnimation("start.json")  // 使用你的 Lottie JSON 檔案
        lottieAnimationView.playAnimation()

        // 設置動畫的時長後自動跳轉，假設動畫持續 3 秒
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToHome()
        }, 2000) // 3000 毫秒 = 3 秒
    } */

    private fun navigateToHome() {
        // 跳轉到 HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
        finish() // 結束當前的 StartActivity
    }
}
