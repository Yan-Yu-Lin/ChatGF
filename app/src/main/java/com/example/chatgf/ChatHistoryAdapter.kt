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
    private val onDeleteClick: (ChatHistory) -> Unit,
    private val isDrawer: Boolean = false // 新增參數來判斷是否為 drawer
) : RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (isDrawer) {
            R.layout.item_drawer_chat_history
        } else {
            R.layout.item_chat_history
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(
            if (view.findViewById<TextView>(R.id.tvDrawerHistoryTitle) != null)
                R.id.tvDrawerHistoryTitle
            else
                R.id.tvHistoryTitle
        )
        val lastMessageText: TextView = view.findViewById(
            if (view.findViewById<TextView>(R.id.tvDrawerLastMessage) != null)
                R.id.tvDrawerLastMessage
            else
                R.id.tvLastMessage
        )
        val deleteButton: ImageButton = view.findViewById(
            if (view.findViewById<ImageButton>(R.id.btnDrawerDelete) != null)
                R.id.btnDrawerDelete
            else
                R.id.btnDelete
        )
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