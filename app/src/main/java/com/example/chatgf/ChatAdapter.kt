package com.example.chatgf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    // Keep reference to the original messages list (the same one used in ChatActivity).
    private val messages: List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // We only skip "system" at display time
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
    }

    /**
     * For convenience, let's define a helper to get the "filtered" list each time.
     */
    private fun getFilteredList(): List<Message> {
        return messages.filter { it.role != "system" }
    }

    override fun getItemCount(): Int {
        // Re-filter every time, so new user/assistant messages are included
        return getFilteredList().size
    }

    override fun getItemViewType(position: Int): Int {
        val filtered = getFilteredList()
        val message = filtered[position]
        return when (message.role) {
            "assistant" -> VIEW_TYPE_ASSISTANT
            else -> VIEW_TYPE_USER // user or any other role
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_message, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_assistant_message, parent, false)
            AssistantViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filtered = getFilteredList()
        val message = filtered[position]
        if (holder is UserViewHolder) {
            holder.bind(message)
        } else if (holder is AssistantViewHolder) {
            holder.bind(message)
        }
    }

    // User bubble
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage = itemView.findViewById<TextView>(R.id.tvUserMessage)
        fun bind(message: Message) {
            tvMessage.text = message.content
        }
    }

    // Assistant bubble
    inner class AssistantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage = itemView.findViewById<TextView>(R.id.tvAssistantMessage)
        fun bind(message: Message) {
            tvMessage.text = message.content
        }
    }
}
