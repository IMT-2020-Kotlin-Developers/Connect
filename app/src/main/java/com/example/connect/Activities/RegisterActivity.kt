package com.example.connect.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.connect.R
import com.example.connect.databinding.RegisterActivityBinding
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

class RegisterActivity : AppCompatActivity(){
    lateinit var binding : RegisterActivityBinding
    lateinit var auth : FirebaseAuth
    val REQUEST_CODE_SIGN_IN = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding  = RegisterActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btSignIn.setOnClickListener {
          registerUser()
        }

        binding.btSinginGoogle.setOnClickListener{
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
            }catch (e: java.lang.Exception){
                Log.d("Google SignIn", e.message.toString())
            }
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                finishAffinity()
            }catch (e: java.lang.Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterActivity,e.message, Toast.LENGTH_SHORT).show()
                }
            }
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
                        startActivity(Intent(this@RegisterActivity, UserDetailActivity::class.java))
                        finishAffinity()
                    }
                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity,e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

        binding.btSinginGoogle.setOnClickListener{
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id))
                .requestEmail()
                .build()

            val signInClient = GoogleSignIn.getClient(application, options)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }
    }
}