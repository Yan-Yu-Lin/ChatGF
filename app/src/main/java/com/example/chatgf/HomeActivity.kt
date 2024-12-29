package com.example.chatgf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnNewChat = findViewById<Button>(R.id.btnNewChat)
        val btnContinue = findViewById<Button>(R.id.btnContinueChat)

        // 「開始新對話」：跳到聊天頁面，但對話紀錄清空或不帶任何舊資料
        btnNewChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            // 這邊若要做「新對話」區分，就可以加一些 flag 或 extra
            startActivity(intent)
        }

        // 「繼續對話」：若有儲存對話紀錄，可帶著舊紀錄到聊天頁面
        btnContinue.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            // 例如把舊紀錄的 key 傳過去
            // intent.putExtra("CHAT_HISTORY_KEY", someData)
            startActivity(intent)
        }
    }
}
