package com.example.chatgf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 先分兩種 ViewType：User / Assistant
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].role) {
            "user" -> VIEW_TYPE_USER
            "assistant" -> VIEW_TYPE_ASSISTANT
            else -> VIEW_TYPE_USER  // 其他角色先預設 user
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
        val message = messages[position]
        if (holder is UserViewHolder) {
            holder.bind(message)
        } else if (holder is AssistantViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    // 使用者訊息的 ViewHolder
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage = itemView.findViewById<TextView>(R.id.tvUserMessage)
        fun bind(message: Message) {
            tvMessage.text = message.content
        }
    }

    // 機器人訊息的 ViewHolder
    inner class AssistantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage = itemView.findViewById<TextView>(R.id.tvAssistantMessage)
        fun bind(message: Message) {
            tvMessage.text = message.content
        }
    }
}
