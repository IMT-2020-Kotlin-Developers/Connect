package com.example.connect.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.model.PostModel
import com.example.connect.R
import com.example.connect.activities.UsersProfile
import com.example.connect.fragments.FeedFragment
import com.example.connect.model.UserModel

class ProfilePostAdapter(private var posts: ArrayList<PostModel>): RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfilePostAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post,parent,false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfilePostAdapter.ViewHolder, position: Int) {
        val post = posts[position]
        Glide.with(holder.itemView.context).load(post.imageUrl).into(holder.postImage)
        holder.postDescription.text = post.description



    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var postImage: ImageView
        var postDescription: TextView
        init {
            this.postImage = row.findViewById(R.id.item_iv)
            this.postDescription = row.findViewById(R.id.item_description)
        }
    }
}

