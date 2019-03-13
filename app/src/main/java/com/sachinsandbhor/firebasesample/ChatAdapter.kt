package com.sachinsandbhor.firebasesample

import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_message.view.*


class ChatAdapter() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var chatMessageList: MutableList<FriendlyMessage> = mutableListOf()
    private var username = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val messageContainer = layoutInflater.inflate(R.layout.item_message, parent, false)
        return ViewHolder(messageContainer)
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(chatMessageList.get(position), username)
    }

    fun add(chatMessage: FriendlyMessage) {
        chatMessageList.add(chatMessage)
        notifyDataSetChanged()
    }

    fun clear() {
        chatMessageList.clear()
    }

    fun addUsername(mUsername: String) {
        username = mUsername
    }

    class ViewHolder(val messageContainer: View) : RecyclerView.ViewHolder(messageContainer) {
        fun setData(message: FriendlyMessage, mUsername: String) {
            Log.e("Chat", mUsername)
            if(message.name.equals(mUsername)) {
                messageContainer.msg_container.gravity = Gravity.RIGHT or Gravity.END
            }else {
                messageContainer.msg_container.gravity = Gravity.LEFT or Gravity.START
            }

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