package com.example.chatgf
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatHistoryAdapter(
    private val histories: List<ChatHistory>,
    private val onHistoryClick: (ChatHistory) -> Unit,
    private val onDeleteClick: (ChatHistory) -> Unit
) : RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.tvHistoryTitle)
        val lastMessageText: TextView = view.findViewById(R.id.tvLastMessage)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = histories[position]
        holder.titleText.text = history.title
        holder.lastMessageText.text = history.lastMessage
        holder.itemView.setOnClickListener { onHistoryClick(history) }
        holder.deleteButton.setOnClickListener { onDeleteClick(history) }
    }

    override fun getItemCount() = histories.size
}