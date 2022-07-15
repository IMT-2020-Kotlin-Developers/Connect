package com.example.connect.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.bind
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.adapter.ProfilePostAdapter
import com.example.connect.databinding.ActivityUserDetailBinding
import com.example.connect.databinding.ActivityUsersProfileBinding
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsersProfile :  AppCompatActivity()  {

    lateinit var binding : ActivityUsersProfileBinding
    lateinit var user : UserModel
    lateinit var auth: FirebaseAuth
    var db = Firebase.firestore.collection("Posts")
    private var itemList = ArrayList<PostModel>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ProfilePostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                user  = getParcelable("User")!!
                binding.tvName.text = user.fullName
                binding.tvBio.text = user.bio
                Glide.with(applicationContext).load(user.photoURL).into(binding.ProfilePic)
            }
        }
        if(user.followers?.indexOf(auth.currentUser?.uid.toString()) != -1){
            binding.btnFollow.text = "Followed"
        }
        else{
            binding.btnFollow.text = "Follow"
        }

        binding.btnFollow.setOnClickListener{
            if(binding.btnFollow.text == "Followed"){

            }
            else{

            }
        }

        itemList = ArrayList()
        recyclerView = binding.postRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)
        db.whereEqualTo("uid",user.uid).get().addOnSuccessListener {
            Log.d("@@Check", "${user.uid}")
            val list : List<DocumentSnapshot> = it.documents
            Log.d("Fetching",list.toString())
            for(data in list){
                val post: PostModel?= data.toObject(PostModel::class.java)
                if(post != null) {
                    Log.d("User", post.description.toString())
                    itemList.add(post)
                }
                else{
                    Log.d("Post","Null")
                }

                adapter.notifyItemInserted(itemList.size - 1)
            }
        }

        adapter = ProfilePostAdapter(itemList)
        recyclerView.adapter = adapter


    }
}