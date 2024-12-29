package com.example.chatgf

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost.Companion.OpenAI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OpenAIManager(apiKey: String) {

    // 你可以在建構子裡接收你想要的參數，例如超時設定等
    private val openAI = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds)
        // 如果要設定更多參數，也可以放到這裡
    )

    /**
     * 呼叫 GPT-3.5-turbo，傳入 userInput，取得回覆。
     * 這是一個掛起函式，需要在協程或 suspend 範圍被呼叫。
     */
    suspend fun getChatResponse(userInput: String): String {
        // 你可先定義好 messages，或在此之前就傳入 messages
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful assistant!"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = userInput
                )
            )
        )

        // 這裡呼叫 API，得到一個 ChatCompletion 物件
        val completion = openAI.chatCompletion(request)
        // 取第一個回覆的 content，如果沒東西則回傳空字串
        return completion.choices.firstOrNull()?.message?.content ?: ""
    }
}