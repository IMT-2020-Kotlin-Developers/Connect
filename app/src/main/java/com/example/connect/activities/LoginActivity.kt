package com.example.connect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connect.R
import com.example.connect.databinding.SignInActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


class LoginActivity :AppCompatActivity() {
   lateinit var binding : SignInActivityBinding
   lateinit var auth : FirebaseAuth
   val REQUEST_CODE_SIGN_IN = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInActivityBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        val view = binding.root
        setContentView(view)


        binding.btLogIn.setOnClickListener {
           logInUser(binding.TextInputEtEmail.text.toString(),binding.TextInputPassword.text.toString())

        }

        binding.btLoginGoogle.setOnClickListener{
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id))
                .requestEmail()
                .build()

            val signInClient = GoogleSignIn.getClient(this, options)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN){
            try{
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                if(account != null)
                    googleAuthForFirebase(account)
            }catch (e:Exception){
                Log.d("Google SignIn", e.message.toString())
            }

        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finishAffinity()
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@LoginActivity,e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logInUser(email : String, password : String) = CoroutineScope(Dispatchers.IO).launch{
      if(email.isNotEmpty() && password.isNotEmpty()){
          try {
              auth.signInWithEmailAndPassword(email,password).await()
              withContext(Dispatchers.Main){
                  startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                  finishAffinity()
              }
          }catch (e : Exception){
              withContext(Dispatchers.Main){
                  Toast.makeText(this@LoginActivity,e.message, Toast.LENGTH_LONG).show()
              }
          }
      }
    }
}