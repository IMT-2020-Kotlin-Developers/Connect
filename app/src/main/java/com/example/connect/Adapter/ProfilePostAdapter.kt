package com.example.connect.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.Model.PostModel
import com.example.connect.R
import com.google.firebase.firestore.auth.User

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

        // For navigation to item details
        //        holder.constraintLayout.setOnClickListener{
        //            val chatUser = User(userDto.userName,userDto.photoUrl,userDto.userId)
        //            val action = UserListFragmentDirections.actionUserListToChat(chatUser)
        //            Navigation.findNavController(holder.constraintLayout).navigate(action)
        //        }
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