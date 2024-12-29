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

    private val openAI = OpenAI(
        token = apiKey,
        timeout = com.aallam.openai.api.http.Timeout(socket = 60.seconds)
        // 如果想設定更多 Timeout，可以另外加 read、connect 等。
    )

    /**
     * 傳入「完整對話歷史」(List<ChatMessage>)，回傳 AI 最新回覆文字。
     * 每次呼叫都帶上先前多輪對話，模型才能記住上下文。
     */
    suspend fun getChatResponse(history: List<ChatMessage>): String {
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = history
        )
        val completion = openAI.chatCompletion(request)
        return completion.choices.firstOrNull()?.message?.content ?: ""
    }
}