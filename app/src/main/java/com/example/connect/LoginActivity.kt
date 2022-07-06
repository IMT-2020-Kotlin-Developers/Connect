package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connect.databinding.SignInActivityBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginActivity :AppCompatActivity() {
   lateinit var binding : SignInActivityBinding
   lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInActivityBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        val view = binding.root
        setContentView(view)


        binding.btLogIn.setOnClickListener {
           logInUser(binding.TextInputEtEmail.text.toString(),binding.TextInputPassword.text.toString())

        }
    }

    private fun logInUser(email : String , password : String) = CoroutineScope(Dispatchers.IO).launch{
      if(email.isNotEmpty() && password.isNotEmpty()){
          try {
              auth.signInWithEmailAndPassword(email,password).await()
              withContext(Dispatchers.Main){
                  startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
              }
          }catch (e : Exception){
              withContext(Dispatchers.Main){
                  Toast.makeText(this@LoginActivity,e.message, Toast.LENGTH_LONG).show()
              }
          }
      }
    }
}