package com.example.connect.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.example.connect.databinding.ActivityUserDetailBinding
import com.example.connect.databinding.ActivityUsersProfileBinding

class UsersProfile :  AppCompatActivity()  {

lateinit var binding : ActivityUsersProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}