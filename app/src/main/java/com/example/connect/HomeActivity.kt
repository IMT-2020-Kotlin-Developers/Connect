package com.example.connect

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.connect.Fragments.FeedFragment
import com.example.connect.Fragments.ProfileFragment
import com.example.connect.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false
        val feedFragment = FeedFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(feedFragment)
        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bnHome -> setCurrentFragment(feedFragment)
                R.id.bnProfile -> {
                    Log.d("Profile","Clicked")
                    setCurrentFragment(profileFragment)
                }
            }
            true
        }


    }
    private fun setCurrentFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        replace(R.id.Fragment_Base,fragment)
        commit()
    }
    }
}