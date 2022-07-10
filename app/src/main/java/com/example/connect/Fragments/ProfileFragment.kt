package com.example.connect.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.Activities.MainActivity
import com.example.connect.Activities.UserDetailActivity
import com.example.connect.Adapter.ProfilePostAdapter
import com.example.connect.Model.PostModel
import com.example.connect.Model.UserModel
import com.example.connect.ViewModel.FireBaseViewModel
import com.example.connect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


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
        viewModel.getCurrentUser()
        viewModel.user().observe(viewLifecycleOwner, Observer {
            binding.tvBio.setText(it.bio.toString())
            binding.tvName.setText(it.fullName.toString())
            Glide.with(this).load(it.photoURL).into(binding.ProfilePic)
            Log.d("@@getobserve", "${it.photoURL}")
            Log.d("@@getobserve", "${it.fullName}")
        })
        binding.ProfilePic.setOnClickListener {

        }
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
        recyclerView.layoutManager = GridLayoutManager(context,2)

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
