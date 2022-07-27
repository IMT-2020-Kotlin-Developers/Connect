package com.example.connect.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.bind
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.adapter.ProfilePostAdapter
import com.example.connect.databinding.ActivityUserDetailBinding
import com.example.connect.databinding.ActivityUsersProfileBinding
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.example.connect.viewModel.FireBaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersProfile :  AppCompatActivity()  {

    lateinit var binding : ActivityUsersProfileBinding
    lateinit var user : UserModel
    lateinit var viewModel: FireBaseViewModel
    lateinit var auth: FirebaseAuth
    var db = Firebase.firestore.collection("Posts")
    var db_User = Firebase.firestore.collection("User")
    private var itemList = ArrayList<PostModel>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ProfilePostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[FireBaseViewModel::class.java]
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                user  = getParcelable("User")!!
                binding.tvName.text = user.fullName
                binding.tvBio.text = user.bio
                Glide.with(applicationContext).load(user.photoURL).into(binding.ProfilePic)
                Log.d("@@USer", "${user}")
            }
        }
        if(user.followers?.indexOf(auth.currentUser?.uid.toString()) == -1){

            binding.btnFollow.text = "Follow"
            Log.d("@@", "${user.followers?.indexOf(auth.currentUser?.uid.toString())}")
        }
        else{
            binding.btnFollow.text = "Followed"
            Log.d("@@", "${user.followers?.indexOf(auth.currentUser?.uid.toString())}")
        }
        var userTemp = UserModel()
        viewModel.user().observe(this){
            userTemp = UserModel(
                it.uid,it.fullName,it.bio,it.photoURL,it.following,it.followers
            )
            Log.d("@@USer", "${user}")

        }
        binding.tvFollowers.text = user.followers?.size.toString()
        binding.tvFollowing.text = user.following?.size.toString()

        binding.btnFollow.setOnClickListener{
            if(binding.btnFollow.text == "Followed"){

                    userTemp.following?.remove(user.uid!!)
                    user.followers?.remove(userTemp.uid!!)
                    viewModel.saveUser(userTemp)
                    viewModel.saveUser(user)
                    binding.btnFollow.text = "Follow"
                    binding.tvFollowing.text = user.following?.size.toString()


            }
            else{

                    userTemp.following?.add(user.uid!!)
                    user.followers?.add(userTemp.uid!!)
                    Log.d("@@Follow?", "${userTemp}")
                    Log.d("@@Follow?", "${user}")
                    viewModel.saveUser(userTemp)
                    viewModel.saveUser(user)
                    binding.btnFollow.text = "Followed"
                    binding.tvFollowers.text = user.followers?.size.toString()

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