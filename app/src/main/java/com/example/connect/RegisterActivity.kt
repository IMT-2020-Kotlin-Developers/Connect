package com.example.connect

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connect.databinding.RegisterActivityBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity(){
    lateinit var binding : RegisterActivityBinding
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding  = RegisterActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btSignIn.setOnClickListener {
          registerUser()
        }
    }
    private fun registerUser() = CoroutineScope(Dispatchers.IO).launch{
        val fullName = binding.TextInputEtFullName.text.toString()
        val email = binding.TextInputEtEmail.text.toString()
        val password = binding.TextInputEtPassword.text.toString()
        val confirmPassword = binding.TextInputConfirmPassword.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
            if(password != confirmPassword){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterActivity,"Password and Confirm password should match", Toast.LENGTH_LONG).show()
                }
            }else if(fullName.isEmpty()){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterActivity,"Please enter Full Name", Toast.LENGTH_LONG).show()
                }
            }
            else{
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        startActivity(Intent(this@RegisterActivity,HomeActivity::class.java))
                    }
                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity,e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
}