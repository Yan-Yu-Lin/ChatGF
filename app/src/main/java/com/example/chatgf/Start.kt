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
        }, 2000) // 延遲 2000 毫秒 (2秒)
    }

    private fun navigateToHome() {
        // 跳轉到 HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
        finish() // 結束當前的 StartActivity
    }
}
