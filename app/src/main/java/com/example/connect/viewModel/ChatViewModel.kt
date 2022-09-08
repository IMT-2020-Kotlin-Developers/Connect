package com.example.connect.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.connect.model.ChatChannelModel
import com.example.connect.model.ChatModel
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    var db = Firebase.firestore.collection("User")
    var dbchannel = Firebase.firestore.collection("Channel")
    var docId : String = "test"
    var auth  : FirebaseAuth = FirebaseAuth.getInstance()
    val userList = ArrayList<UserModel>()
    private var followingList = MutableLiveData<ArrayList<UserModel>>()
    init {
    getFollowers()
    }
    fun following() : MutableLiveData<ArrayList<UserModel>> {
        return followingList
    }
   suspend fun getDocId(channel : ChatChannelModel) : String{
        try {
            val personQuery1 = dbchannel
                .whereEqualTo("person1st",channel.Person1st)
                .whereEqualTo("person2nd",channel.Person2nd)
                .get()
                .await()
            val personQuery2 = dbchannel
                .whereEqualTo("person2nd",channel.Person1st)
                .whereEqualTo("person1st",channel.Person2nd)
                .get()
                .await()
           for(data in personQuery1){
               if(data.exists()){
                   docId = data.id
               }
           }
            for(data in personQuery2){
                if(data.exists()){
                    docId = data.id
                }
            }


        }catch (e : Exception){
            Log.d("@@Error",e.message!!)
        }
       return docId
    }
    fun getFollowers() = CoroutineScope(Dispatchers.IO).launch {
        var listOfFollowers = ArrayList<String>()
        db.get().addOnSuccessListener {
            val list: List<DocumentSnapshot> = it.documents
            for (currUser in list) {
                val User: UserModel? = currUser.toObject(UserModel::class.java)
                if(User?.uid == auth.currentUser?.uid){
                    listOfFollowers = User?.following!!
                }
            }
            for(item in listOfFollowers){
                db.whereEqualTo("uid", item).get().addOnSuccessListener {
                    val list: List<DocumentSnapshot> = it.documents
                    for(data in list){
                        val user: UserModel? = data.toObject(UserModel::class.java)
                        if(user != null){
                            userList.add(user)
                        }else {
                            Log.d("Post", "Null")
                        }
                    }
                    followingList.postValue(userList)
                }
            }

        }
    }
    fun createChannel(channel : ChatChannelModel, chat : ChatModel) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val personQuery1 = dbchannel
                .whereEqualTo("person1st",channel.Person1st)
                .whereEqualTo("person2nd",channel.Person2nd)
                .get()
                .await()
            val personQuery2 = dbchannel
                .whereEqualTo("person1st",channel.Person2nd)
                .whereEqualTo("person2nd",channel.Person1st)
                .get()
                .await()
            if(personQuery1.documents.isEmpty()){
                dbchannel.add(channel)
            }
            if(personQuery2.documents.isEmpty()){
                var newChannel = ChatChannelModel(channel.Person2nd,channel.Person1st)
                dbchannel.add(newChannel)
            }
            for(data in personQuery1.documents){
                val list = personQuery1.documents
                Log.d("@@Success",list.toString())
                for(document in list){
                    var dbchat = Firebase.firestore.collection("/Channel/${document.id}/Messages")
                    try {
                        dbchat?.add(chat).addOnSuccessListener {
                            Log.d("@@CreateChannel" , "Added")
                        }

                    }catch (e : Exception) {
                        Log.d("@@ErrorInInsertChat" , e.message.toString())
                    }
                    Log.d("@@CreateChannel",document.id)
                }
            }

            for(data in personQuery2.documents){
                val list  = personQuery2.documents
                for(document in list){
                    var dbchat = Firebase.firestore.collection("/Channel/${document.id}/Messages")
                    try {
                        dbchat?.add(chat).addOnSuccessListener {
                            Log.d("@@CreateChannel" , "Added")
                        }

                    }catch (e : Exception) {
                        Log.d("@@ErrorInInsertChat" , e.message.toString())
                    }
                }
            }
        }catch (e : Exception){
            Log.d("@@ErrorInCreateChannel", "createChannel: ${e.message}")
        }
    }

   suspend fun getChats( personOneid : String , personTwoid : String) : ArrayList<ChatModel> {
        var chatList = ArrayList<ChatModel>()
       val channel = ChatChannelModel(personOneid,personTwoid)
       getDocId(channel)
       Log.d("@@getChats" , "${docId}")
       var dbCHAT = Firebase.firestore.collection("/Channel/${docId}/Messages")
       try {
          val pq =  dbCHAT.get().await()
           for(data in pq){
             var chat = data.toObject(ChatModel::class.java)
               chatList.add(chat)
           }

       }catch (e : Exception){
           Log.d("@@getChats", e.message!!)
       }
       Log.d("@@getChats" , chatList.toString())
       return chatList
    }

}