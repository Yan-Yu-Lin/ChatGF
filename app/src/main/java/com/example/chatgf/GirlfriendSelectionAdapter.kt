package com.example.chatgf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GirlfriendSelectionAdapter(
    private val girlfriends: List<GirlfriendType>,
    private val onSelect: (GirlfriendType) -> Unit
) : RecyclerView.Adapter<GirlfriendSelectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.ivAvatar)
        val name: TextView = view.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_girlfriend_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val girlfriend = girlfriends[position]
        holder.avatar.setImageResource(girlfriend.avatarResId)
        holder.name.text = girlfriend.displayName
        holder.itemView.setOnClickListener { onSelect(girlfriend) }
    }

    override fun getItemCount() = girlfriends.size
}