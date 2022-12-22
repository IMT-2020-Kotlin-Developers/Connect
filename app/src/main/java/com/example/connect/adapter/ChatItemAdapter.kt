package com.example.connect.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.R
import com.example.connect.model.ChatModel
import com.example.connect.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.GravityCompat

import androidx.constraintlayout.widget.ConstraintLayout


class ChatItemAdapter(private var chat : ArrayList<ChatModel>) : RecyclerView.Adapter<ChatItemAdapter.ViewHolder>() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
//        val date : TextView
        val message : TextView
        val layoutP : FrameLayout
        val layout : ConstraintLayout
        init {
            this.message = view.findViewById(R.id.message)
//            this.date = view.findViewById(R.id.tv_Date)
            this.layoutP = view.findViewById(R.id.parent_layout)
            this.layout = view.findViewById(R.id.layout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item,parent,false)
        return ChatItemAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentChat = chat[position]

        if(currentChat.senderId == auth.currentUser?.uid){
            holder.layout.apply {
                setBackgroundResource(R.drawable.chat_shape_primary)
                val lParms = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.END).apply {
                    bottomMargin = 10
                    topMargin = 10
                    leftMargin = 10
                    rightMargin = 10
                }
               this.layoutParams = lParms
            }
        }else{
            holder.layout.apply {
                setBackgroundResource(R.drawable.chat_shape_white)
                val lParms = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.START).apply {
                    bottomMargin = 10
                    topMargin = 10
                    leftMargin = 10
                    rightMargin = 10
                }
                this.layoutParams = lParms
            }
        }
        holder.message.text = currentChat.chat
//        holder.date.text = currentChat.date
    }
    fun updateAdapter(mChat: ArrayList<ChatModel>) {
        this.chat = mChat
        notifyDataSetChanged()
    }
    fun clearAdapter() {
        this.chat.clear()
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
       return chat.size
    }
}