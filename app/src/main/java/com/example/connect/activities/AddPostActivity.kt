package com.example.connect.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.connect.model.PostModel
import com.example.connect.databinding.ActivityAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class AddPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPostBinding
    lateinit var uri:Uri
    val IMAGE_PICKER_CODE = 100
    private var picked = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    var db = Firebase.firestore.collection("Posts")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()
        binding.cardView.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICKER_CODE)
        }
        binding.btDone.setOnClickListener{
            Log.d("","Clicked")
            if(picked == 0){
                Toast.makeText(this,"Choose image",Toast.LENGTH_SHORT).show()
            }
            else if(binding.etText.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter text",Toast.LENGTH_SHORT).show()
            }
            else{
                binding.btDone.visibility = View.GONE
                binding.pbAddPost.visibility = View.VISIBLE
                val postReference = storageReference.child("posts/${System.currentTimeMillis()}-photo.jpg")
                CoroutineScope(Dispatchers.IO).launch {
                    postReference.putFile(uri).addOnSuccessListener {
                        postReference.downloadUrl.addOnSuccessListener {
                            Log.d("Post Upload",it.toString())
                            val post = PostModel(auth.currentUser?.uid,it.toString(),binding.etText.text.toString(),System.currentTimeMillis())
                            db.add(post).addOnSuccessListener {
                                Log.d("Post Upload","Success")
                                finish()
                            }.addOnFailureListener{
                                binding.pbAddPost.visibility = View.GONE
                                binding.btDone.visibility = View.VISIBLE
                                Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener{
                            binding.pbAddPost.visibility = View.GONE
                            binding.btDone.visibility = View.VISIBLE
                            Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener{
                        binding.pbAddPost.visibility = View.GONE
                        binding.btDone.visibility = View.VISIBLE
                        Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_PICKER_CODE && resultCode == Activity.RESULT_OK && data != null){
            uri = data.data!!
            try {
                launchImageCrop(uri)
            } catch (e: Exception) {
                Log.d("Image picker",e.message.toString())
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri = CropImage.getActivityResult(data).uri
            Glide.with(applicationContext).load(uri).into(binding.ivImage)
            picked = 1
        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1080, 720)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }
}