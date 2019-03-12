package com.sachinsandbhor.firebasesample

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_message.view.*

class ChatAdapter() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var chatMessageList: MutableList<FriendlyMessage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val messageContainer = layoutInflater.inflate(R.layout.item_message, parent, false)
        return ViewHolder(messageContainer)
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(chatMessageList.get(position))
    }

    fun add(chatMessage: FriendlyMessage) {
        chatMessageList.add(chatMessage)
        notifyDataSetChanged()
    }

    class ViewHolder(val messageContainer: View) : RecyclerView.ViewHolder(messageContainer) {
        fun setData(message: FriendlyMessage) {
            var isPhoto:Boolean = message.photoUrl != null && message.photoUrl != ""
            if(isPhoto) {
                //messageTextView
                messageContainer.messageTextView.visibility = View.GONE
                messageContainer.photoImageView.visibility = View.VISIBLE
                Picasso.get().load(message.photoUrl).into(messageContainer.photoImageView)
                messageContainer.nameTextView.text = message.name
            } else{
                messageContainer.messageTextView.visibility = View.VISIBLE
                messageContainer.photoImageView.visibility = View.GONE
                messageContainer.messageTextView.text = message.text
                messageContainer.nameTextView.text = message.name
            }
        }

    }
}