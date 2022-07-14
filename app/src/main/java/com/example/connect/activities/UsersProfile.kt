package com.example.connect.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.bind
import androidx.databinding.DataBindingUtil.setContentView
import com.bumptech.glide.Glide
import com.example.connect.databinding.ActivityUserDetailBinding
import com.example.connect.databinding.ActivityUsersProfileBinding
import com.example.connect.model.UserModel

class UsersProfile :  AppCompatActivity()  {

lateinit var binding : ActivityUsersProfileBinding
lateinit var user : UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                user  = getParcelable("User")!!
                binding.tvName.text = user.fullName
                binding.tvBio.text = user.bio
                Glide.with(applicationContext).load(user.photoURL).into(binding.ProfilePic)
            }
        }


    }
}