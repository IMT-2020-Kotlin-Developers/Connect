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
import com.example.connect.MainActivity
import com.example.connect.UserModel
import com.example.connect.ViewModel.FireBaseViewModel
import com.example.connect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: FireBaseViewModel

    lateinit var imageUri: Uri
    var db = Firebase.firestore.collection("User")
    var storageReference = Firebase.storage.reference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(activity!!)[FireBaseViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        viewModel.getCurrentUser()
        viewModel.user().observe(viewLifecycleOwner, Observer {
            binding.TextInputEtBio.setText(it.bio.toString())
            binding.TextInputEtName.setText(it.fullName.toString())
            Glide.with(this).load(it.photoURL).into(binding.ProfilePic)
            Log.d("@@getobserve", "${it.photoURL}")
            Log.d("@@getobserve", "${it.fullName}")
        })
        binding.btLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, MainActivity::class.java))
        }

        binding.ProfilePic.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
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
            var User = UserModel(auth.uid, "", "", "")
            User.fullName = binding.TextInputEtName.text.toString()
            User.bio = binding.TextInputEtBio.text.toString()
            User.photoURL = auth.currentUser?.photoUrl.toString()
            viewModel.saveUser(User)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            imageUri = data?.data!!
            viewModel.uploadProfilePic(imageUri)
            binding.ProfilePic.setImageURI(imageUri)
        }
//        binding.ProfilePic.setImageURI(null)
    }


}
