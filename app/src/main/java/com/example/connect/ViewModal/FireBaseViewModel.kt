package com.example.connect.ViewModal

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.connect.UserModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.net.URI

class FireBaseViewModel(application: Application) : AndroidViewModel(application) {
     var auth : FirebaseAuth = FirebaseAuth.getInstance()
     var db = Firebase.firestore.collection("User")
     var storageReference = Firebase.storage.reference

  fun saveUser(user : UserModal) = CoroutineScope(Dispatchers.IO).launch{
       try {
          val personQuery = db
               .whereEqualTo("uid",user.uid)
               .get()
               .await()
            if(personQuery.documents.isNotEmpty()){
                 try {
                      for(Doc in personQuery){
                           db.document(Doc.id).set(user, SetOptions.merge()).await()
                          withContext(Dispatchers.Main){
                              Toast.makeText(getApplication(),"Success", Toast.LENGTH_LONG).show()
                          }
                      }

                 }catch (e : Exception){
                      withContext(Dispatchers.Main){
                           Toast.makeText(getApplication(),e.message, Toast.LENGTH_LONG).show()
                      }
                 }
            }else{
                 try{
                 db.add(user).await()
                     withContext(Dispatchers.Main){
                         Toast.makeText(getApplication(),"Success", Toast.LENGTH_LONG).show()
                     }
                 }catch (e : Exception){
                      withContext(Dispatchers.Main){
                           Toast.makeText(getApplication(),e.message, Toast.LENGTH_LONG).show()
                      }
                 }
            }
       }catch (e : Exception){
            withContext(Dispatchers.Main){
                 Log.d("@@@@",e.message!!)
            }
       }

  }

     fun uploadProfilePic(uri : Uri)  {
         var Uid = auth.currentUser?.uid

          CoroutineScope(Dispatchers.IO).launch{

         try {
               storageReference.child("$Uid/profilePic").putFile(uri).await()
                 Log.d("@@@@","Uploaded")
          }catch (e : Exception){
               withContext(Dispatchers.Main){
                    Toast.makeText(getApplication(),e.message, Toast.LENGTH_LONG).show()
               }
          }
     }

}

     fun getCurrentUser() : UserModal {
         var User : UserModal? = null
         var user = auth.currentUser
         CoroutineScope(Dispatchers.IO).launch {
             val personQuery = db
                 .whereEqualTo("uid",user?.uid)
                 .get()
                 .await()
                     for (doc in personQuery){
                         User?.uid = doc.get("uid").toString()
                         User?.bio = doc.get("bio").toString()
                         User?.fullName = doc.get("fullName").toString()
                         User?.photoURL = doc.get("photoURL").toString()
                     }


         }

         return User!!
     }
}