package com.example.connect.Fragments

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.connect.MainActivity
import com.example.connect.UserModal
import com.example.connect.ViewModal.FireBaseViewModel
import com.example.connect.ViewModal.FireBaseViewModelFactory
import com.example.connect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class ProfileFragment : Fragment() {

lateinit var binding: FragmentProfileBinding
lateinit var auth : FirebaseAuth
lateinit var viewModel : FireBaseViewModel
lateinit var viewModelFactory : FireBaseViewModelFactory
lateinit var   imageUri : Uri
 lateinit var User : UserModal
    var db = Firebase.firestore.collection("User")
    var storageReference = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        viewModelFactory = FireBaseViewModelFactory(application = Application())
        viewModel = ViewModelProvider(this,viewModelFactory)[FireBaseViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        User = UserModal("","","","")
        User.uid = auth.currentUser?.uid
        binding.btLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context,MainActivity::class.java))
        }
        binding.ProfilePic.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Update Profile Picture?")
            builder.setMessage("open gallery")
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            }
            builder.setNegativeButton("No"){dialogInterface, which ->
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
        binding.btDone.setOnClickListener {
           User.fullName = binding.textInputLayoutName.editText?.text.toString()
            User.bio = binding.textInputLayoutBio.editText?.text.toString()
            Toast.makeText(context,"${User.photoURL + User.fullName}", Toast.LENGTH_LONG).show()
            viewModel.saveUser(User)
//            updateUI()
        }

updateUI()
        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
           imageUri = data?.data!!
        }
        uploadProfilePic(imageUri)
//        getUrl()
        binding.ProfilePic.setImageURI(null)
        binding.ProfilePic.setImageURI(imageUri)
    }
    fun uploadProfilePic(uri : Uri)  {
        var Uid = auth.currentUser?.uid

        CoroutineScope(Dispatchers.IO).launch{

            try {
                storageReference.child("$Uid/profilePic").putFile(uri).await()
                User.photoURL =  storageReference.child("${auth.currentUser?.uid}/profilePic").downloadUrl.await().toString()
                Log.d("@@@@","${User.photoURL}")
                updateUI()
            }catch (e : Exception){
                withContext(Dispatchers.Main){
                    Log.d("@@@@",e.message.toString())
                }
            }
        }

    }
    private fun getUrl() =CoroutineScope(Dispatchers.IO).launch{
        try {

            Log.d("@@@@","Fetched")
        }catch (e : Exception){
            Log.d("@@@@",e.message.toString())
        }

        }


    private fun updateUI(){

        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = db
                .whereEqualTo("uid",auth.currentUser?.uid)
                .get()
                .await()
            for (doc in personQuery){
                User?.uid = doc.get("uid").toString()
                User?.bio = doc.get("bio").toString()
                User?.fullName = doc.get("fullName").toString()
                User?.photoURL = doc.get("photoURL").toString()
            }

            withContext(Dispatchers.Main){
                binding.TextInputEtBio.setText(User.bio)
                binding.TextInputEtName.setText(User.fullName)
                Glide.with(view!!.context).load(User.photoURL).into(binding.ProfilePic)
            }
        }
      }
    }
