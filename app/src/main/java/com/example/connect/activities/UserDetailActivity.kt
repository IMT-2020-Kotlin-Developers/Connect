package com.example.connect.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class UserDetailActivity :  AppCompatActivity() {
    lateinit var binding : ActivityUserDetailBinding
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: FireBaseViewModel
    lateinit var imageUri: Uri
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
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
        binding.btDone.setOnClickListener {
            User.uid = auth.currentUser?.uid
            User.fullName = binding.TextInputEtName.text.toString()
            User.bio = binding.TextInputEtBio.text.toString()
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

            CoroutineScope(Dispatchers.IO).launch {
                User.photoURL =    viewModel.uploadProfilePic(imageUri)

                Log.d("@@Download and assigned", "${User.photoURL}")
            }

        }

    }
}