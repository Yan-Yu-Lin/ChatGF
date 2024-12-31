package com.example.chatgf

/**
 * 定義女友類型與對應的 system prompt (暫時用 placeholder)。
 */
enum class GirlfriendType(
    val displayName: String,
    val systemPrompt: String
) {
    GIRL_1(
        displayName = "Girl 1",
        systemPrompt = "Placeholder for Girl 1"
    ),
    GIRL_2(
        displayName = "Girl 2",
        systemPrompt = "Placeholder for Girl 2"
    ),
    GIRL_3(
        displayName = "Girl 3",
        systemPrompt = "Placeholder for Girl 3"
    ),
    RANDOM(
        displayName = "Random",
        systemPrompt = "Placeholder (will pick actual random girlfriend)"
    );

    companion object {
        /**
         * 從 GIRL_1, GIRL_2, GIRL_3 裡隨機挑一位
         */
        fun pickRandomGirlfriend(): GirlfriendType {
            val candidates = listOf(GIRL_1, GIRL_2, GIRL_3)
            return candidates.random()
        }
    }
}
