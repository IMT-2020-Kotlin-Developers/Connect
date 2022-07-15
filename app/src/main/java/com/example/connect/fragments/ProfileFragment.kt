package com.example.connect.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.activities.MainActivity
import com.example.connect.activities.UserDetailActivity
import com.example.connect.adapter.ProfilePostAdapter
import com.example.connect.model.PostModel
import com.example.connect.viewModel.FireBaseViewModel
import com.example.connect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: FireBaseViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ProfilePostAdapter
    private var itemList = ArrayList<PostModel>()
    lateinit var imageUri: Uri
    var db = Firebase.firestore.collection("Posts")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(activity!!)[FireBaseViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        viewModel.user().observe(viewLifecycleOwner, Observer {
            binding.tvBio.setText(it.bio.toString())
            binding.tvName.setText(it.fullName.toString())
            Glide.with(this).load(it.photoURL).into(binding.ProfilePic)
            binding.tvFollowers.text = it.followers?.size.toString()
            binding.tvFollowing.text = it.following?.size.toString()
            Log.d("@@getobserve", "${it.photoURL}")
            Log.d("@@getobserve", "${it.fullName}")
        })


        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context,MainActivity::class.java))
            activity!!.finish()
        }
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(context,UserDetailActivity::class.java))
        }


        // Reyclerview without view model
        itemList = ArrayList()
        recyclerView = binding.postRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.layoutManager = GridLayoutManager(context,2)

        db.whereEqualTo("uid",auth.currentUser?.uid).get().addOnSuccessListener {
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
//                    mExampleAdapter.setItems(list);

//                    mExampleAdapter.notifyDataSetChanged()
                adapter.notifyItemInserted(itemList.size - 1)
            }
        }

        adapter = ProfilePostAdapter(itemList)
        recyclerView.adapter = adapter


        return binding.root
    }

}
