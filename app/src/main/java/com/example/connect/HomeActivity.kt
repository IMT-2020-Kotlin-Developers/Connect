package com.example.connect

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.connect.Fragments.FeedFragment
import com.example.connect.Fragments.ProfileFragment
import com.example.connect.ViewModel.HomeActivityViewModel
import com.example.connect.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false

        val viewModel: HomeActivityViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
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
            }
            true
        }


    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.Fragment_Base, fragment)
            commit()
        }
    }
}