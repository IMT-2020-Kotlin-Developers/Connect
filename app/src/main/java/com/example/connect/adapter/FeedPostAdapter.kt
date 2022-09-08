package com.example.connect.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.R
import com.example.connect.activities.UsersProfile
import com.example.connect.glide.GlideApp
import com.example.connect.model.PostModel
import com.google.firebase.firestore.auth.User
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.connect.activities.HomeActivity
import com.example.connect.model.LikeModel
import com.example.connect.viewModel.PostViewModel
import com.google.firebase.auth.FirebaseAuth


class FeedPostAdapter(private var posts : ArrayList<PostModel>)
    : RecyclerView.Adapter<FeedPostAdapter.ViewHolder>(){
    var likedUser = LikeModel()
    lateinit var auth : FirebaseAuth
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        auth = FirebaseAuth.getInstance()
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed,parent,false)

        return ViewHolder(itemView)
    }
    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var postImage: ImageView = row.findViewById(R.id.item_iv)
        var postProfilePic : ImageView  = row.findViewById(R.id.iv_itemProfilePic)
        var postDescription: TextView = row.findViewById(R.id.item_description)
        var postFullName : TextView  = row.findViewById(R.id.iv_itemFullName)
//        var postLikes : ImageView = row.findViewById(R.id.iv_heart)
//        var postComment : ImageView = row.findViewById(R.id.iv_comments)
//        var postNumberLikes : TextView = row.findViewById(R.id.tv_likes)
//        var postNumberComments : TextView = row.findViewById(R.id.tv_comments)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val post = posts[position]
        Log.d("@@@", "onBindViewHolder: $post ")
        holder.postFullName.text  = post.fullName
        GlideApp.with(holder.itemView.context).load(post.photoProfileUrl).into(holder.postProfilePic)
        GlideApp.with(holder.itemView.context).load(post.imageUrl).into(holder.postImage)
        holder.postDescription.text = post.description
//        holder.postLikes.setOnClickListener {
//            val context = holder.itemView.context
//            val imageView : ImageView = it as ImageView
//            imageView.setImageResource(R.drawable.ic_vector_likes_red)
//            val send = Intent(context , HomeActivity::class.java)
//            likedUser.likeNumber++
//            likedUser.uid = auth.uid.toString()
//            send.putExtra("User_who_liked",likedUser)
//            context.startActivity(send)
//        }

    }
    fun updateAdapter(mPosts: ArrayList<PostModel>) {
        this.posts = mPosts
       notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return posts.size
    }

}