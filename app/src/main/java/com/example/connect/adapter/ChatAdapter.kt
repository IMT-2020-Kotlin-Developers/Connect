package com.example.connect.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.R
import com.example.connect.activities.ChatActivity

import com.example.connect.model.UserModel

class ChatAdapter(private var user : ArrayList<UserModel>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user,parent,false)
        return ChatAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val profilePic : ImageView
        val fullName : TextView
        init {
            this.profilePic = view.findViewById(R.id.iv_itemProfilePic)
            this.fullName = view.findViewById(R.id.tv_itemName)
        }
    }
    fun updateAdapter(mUser: ArrayList<UserModel>) {
        this.user = mUser
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val User = user[position]
        Glide.with(holder.itemView.context).load(User.photoURL).into(holder.profilePic)
        holder.fullName.text = User.fullName
        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            val send = Intent(context , ChatActivity::class.java)
            send.putExtra("User",User)
            context.startActivity(send)
        }
    }
}