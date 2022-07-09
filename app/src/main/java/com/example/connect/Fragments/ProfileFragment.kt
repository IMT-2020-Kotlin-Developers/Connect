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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.connect.Activities.MainActivity
import com.example.connect.Activities.UserDetailActivity
import com.example.connect.Model.UserModel
import com.example.connect.ViewModel.FireBaseViewModel
import com.example.connect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: FireBaseViewModel

    lateinit var imageUri: Uri
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
        }
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(context,UserDetailActivity::class.java))
        }
        return binding.root
    }

}
