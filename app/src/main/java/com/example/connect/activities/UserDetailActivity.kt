package com.example.connect.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.connect.model.UserModel
import com.example.connect.viewModel.FireBaseViewModel
import com.example.connect.databinding.ActivityUserDetailBinding
import com.example.connect.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserDetailActivity :  AppCompatActivity() {
    lateinit var binding : ActivityUserDetailBinding
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: FireBaseViewModel
    lateinit var imageUri: Uri
    var storageReference = Firebase.storage.reference
    var User = UserModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[FireBaseViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[FireBaseViewModel::class.java]
        viewModel.user().observe(this, Observer {
            if(it.bio == null)
                it.bio=""
            if(it.fullName ==null)
                it.fullName=""

            binding.TextInputEtBio.setText(it.bio.toString())
            binding.TextInputEtName.setText(it.fullName.toString())
            Glide.with(this).load(it.photoURL).into(binding.ProfilePic)
            User.photoURL = it.photoURL
        })
        binding.ProfilePic.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update Profile Picture?")
            builder.setMessage("open gallery")
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
            }
            val alertDialog: AlertDialog = builder.create()
            binding.btDone.visibility = View.GONE
            binding.progressBarImageLoading.visibility = View.VISIBLE
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
        binding.btDone.setOnClickListener {
            User.uid = auth.currentUser?.uid
            User.fullName = binding.TextInputEtName.text.toString()
            User.bio = binding.TextInputEtBio.text.toString()
            User.following = viewModel.user().value?.following
            User.followers = viewModel.user().value?.followers

            Log.d("@@onDoneClick", "${User}")
            viewModel.saveUser(User)
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            imageUri = data?.data!!

            Glide.with(this).load(imageUri).into(binding.ProfilePic)


//                User.photoURL =    viewModel.uploadProfilePic(imageUri)
                try {
                    storageReference.child("${auth.currentUser?.uid}/profilePic").putFile(imageUri)
                        .addOnSuccessListener {
                            storageReference
                                .child("${auth.currentUser?.uid}/profilePic")
                                .downloadUrl.addOnSuccessListener {
                                    User.photoURL = it.toString()
                                    binding.progressBarImageLoading.visibility = View.GONE
                                    binding.btDone.visibility = View.VISIBLE
                                    Log.d("@@Imgae", "${User.photoURL}")
                                }
                                .addOnFailureListener {
                                    Log.d("@@@@", it.message.toString())
                                }
                        }
                        .addOnFailureListener{
                            Log.d("@@@@", it.message.toString())
                        }
                } catch (e: Exception) {
                    Log.d("@@@@", e.message.toString())
                }

            Log.d("@@Download and assigned", "${User.photoURL}")

        }

    }
}