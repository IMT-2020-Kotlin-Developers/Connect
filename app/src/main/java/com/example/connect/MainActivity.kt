package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.example.connect.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
lateinit var binding: ActivityMainBinding
lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
   binding.btLogIn.setOnClickListener {
       startActivity(Intent(this,LoginActivity::class.java))
   }
        binding.btSignIn.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        if(auth.currentUser != null){
            binding.progressBar2.visibility = View.VISIBLE
            binding.btSignIn.visibility = View.GONE
            binding.btLogIn.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                startActivity(Intent(this , HomeActivity::class.java))
            finish()
            }, 1000)

        }
    }


}