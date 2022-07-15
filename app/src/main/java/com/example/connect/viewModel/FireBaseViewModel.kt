package com.example.connect.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.connect.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FireBaseViewModel(application: Application) : AndroidViewModel(application) {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var db = Firebase.firestore.collection("User")
    var storageReference = Firebase.storage.reference
    private var currentUser = MutableLiveData<UserModel>(UserModel("","","",""))
    var URL = ""
    init {
        getCurrentUser()
    }
    fun user(): LiveData<UserModel> {
        return currentUser
    }

    fun saveUser(user: UserModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val personQuery = db
                .whereEqualTo("uid", user.uid)
                .get()
                .await()
            if (personQuery.documents.isNotEmpty()) {
                try {
                    for (Doc in personQuery) {
                        db.document(Doc.id).set(user, SetOptions.merge()).await()
                        Log.d("@@Save", "${user}")
                    }
                    currentUser = MutableLiveData(user)
                } catch (e: Exception) {
                }
            } else {
                try {
                    db.add(user).await()
                    currentUser = MutableLiveData(user)
                } catch (e: Exception) {

                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("@@@@", e.message!!)
            }
        }

    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            var user = UserModel()
            val personQuery = db
                .whereEqualTo("uid", auth.currentUser?.uid)
                .get()
                .await()
            for (doc in personQuery) {
                user = UserModel(
                    doc.get("uid").toString(),
                    doc.get("fullName").toString(),
                    doc.get("bio").toString(),
                    doc.get("photoURL").toString(),
                    arrayListOf(doc.get("following").toString()),
                    arrayListOf(doc.get("followers").toString())
                )


                Log.d("@@GetCurrentUser", "${user}")
            }
            currentUser.postValue(user)
        }
    }

   suspend fun uploadProfilePic(uri: Uri) : String{
        val Uid = auth.currentUser?.uid

            try {
                storageReference.child("$Uid/profilePic").putFile(uri).await()
                URL = storageReference
                    .child("${auth.currentUser?.uid}/profilePic")
                    .downloadUrl.await().toString()
                Log.d("@@Imgae", "$URL")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("@@@@", e.message.toString())
                }
            }
       return URL
    }
}



