package com.example.connect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.connect.fragments.FeedFragment
import com.example.connect.fragments.ProfileFragment
import com.example.connect.R
import com.example.connect.viewModel.HomeActivityViewModel
import com.example.connect.databinding.ActivityHomeBinding
import com.example.connect.fragments.ChatFragment
import com.example.connect.fragments.NotificationFragment

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false

        val viewModel:  HomeActivityViewModel= ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        viewModel.fragment().observe(this, Observer {
            setCurrentFragment(it)
        })

//        setCurrentFragment(feedFragment)
        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bnHome -> viewModel.setCurrFragment(FeedFragment())
                R.id.bnProfile -> {
                    Log.d("Profile", "Clicked")
                    viewModel.setCurrFragment(ProfileFragment())
                }
                R.id.bnChat -> {
                    viewModel.setCurrFragment(ChatFragment())
                }
                R.id.bnNotification-> {
                    viewModel.setCurrFragment(NotificationFragment())
                }
            }
            true
        }

        binding.FloatingActionButton.setOnClickListener{
            startActivity(Intent(this@HomeActivity, AddPostActivity::class.java))
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.Fragment_Base, fragment)
            commit()
        }
    }
}