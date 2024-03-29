package com.example.connect.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.R
import com.example.connect.activities.UsersProfile
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.google.firebase.firestore.auth.User

class UserListAdapter(private var user : ArrayList<UserModel>): RecyclerView.Adapter<UserListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
      val itemView = LayoutInflater.from(parent.context)
          .inflate(R.layout.item_user,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        val User = user[position]
        Glide.with(holder.itemView.context).load(User.photoURL).into(holder.profilePic)
        holder.fullName.text = User.fullName

        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            val send = Intent(context , UsersProfile::class.java)
            send.putExtra("User",User)
            context.startActivity(send)

        }

    }
    fun updateAdapter(mUser: ArrayList<UserModel>) {
        this.user = mUser
        notifyDataSetChanged()
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


    }