package com.example.connect.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.adapter.ChatItemAdapter
import com.example.connect.adapter.ProfilePostAdapter
import com.example.connect.databinding.ActivityChatBinding
import com.example.connect.glide.GlideApp
import com.example.connect.model.ChatChannelModel
import com.example.connect.model.ChatModel
import com.example.connect.model.UserModel
import com.example.connect.retrofit.NotificationModel
import com.example.connect.retrofit.PushNotification
import com.example.connect.retrofit.RetrofitInstance
import com.example.connect.viewModel.ChatViewModel
import com.example.connect.viewModel.FireBaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
const val TOPIC = "/topics/myTopic"

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    var user = UserModel()
    lateinit var viewModel : ChatViewModel
    lateinit var viewModelUser : FireBaseViewModel
    lateinit var auth : FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : ChatItemAdapter
    var chat = ChatModel()
    var listOfChat = ArrayList<ChatModel>()
    private var currentUser = UserModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        binding = ActivityChatBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        viewModelUser = ViewModelProvider(this)[FireBaseViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        recyclerView = binding.chatRecycleView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        viewModelUser.user().observe(this, androidx.lifecycle.Observer {
            currentUser = it
        })
        val view = binding.root
        setContentView(view)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
               user = getParcelable("User")!!
                binding.tvFullName.text = user.fullName
                GlideApp.with(applicationContext).load(user.photoURL).into(binding.ivItemProfilePicChat)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
//            listOfChat = viewModel.getChats(auth.currentUser?.uid.toString(),user.uid.toString())
            refreshChat()

//            adapter.updateAdapter(listOfChat)
        }

        binding.textInputMessageLayout.setEndIconOnClickListener {
            val message = binding.TextInputEtChat.text.toString()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val channel = ChatChannelModel(auth.currentUser?.uid,user.uid)
            chat = ChatModel(message,auth.currentUser?.uid,user.uid,currentDate, System.currentTimeMillis())
            viewModel.createChannel(channel, chat)
            val title = "Message from ${currentUser.fullName}"
            val body = "Click to open application"
            PushNotification(NotificationModel(title,body) , TOPIC)
                .also {
                    sendNotification(it)
                }
            binding.TextInputEtChat.setText("")
        }

        adapter = ChatItemAdapter(listOfChat)
        recyclerView.adapter = adapter
    }

   suspend fun refreshChat() {
       val channel = ChatChannelModel(auth.currentUser?.uid,user.uid)
       val docID =   viewModel.getDocId(channel)

       var dbCHAT = Firebase.firestore.collection("/Channel/${docID}/Messages")
       try {
          var pq =  dbCHAT.addSnapshotListener { querySnapshot, FirebaseException ->
              Log.d("@@refreshChat here","CALLED")
              FirebaseException?.let {
                  Log.d("@@refreshChat", it.message!!)
              }

              querySnapshot?.let {

                  var q = it.documentChanges
                  if(q == null){
                      for(data in it){
                          var chat = data.toObject(ChatModel::class.java)
                          listOfChat.add(chat)
                      }
                  }else{
                      for (data in q) {
                          if(data.type == DocumentChange.Type.ADDED){
                              var chat = data.document
                              var newChat = chat.toObject(ChatModel::class.java)
                              listOfChat.add(newChat)
                          }
                          var sortedList = listOfChat.sortedWith(compareBy({ it.time }))
                          var newList = ArrayList<ChatModel>()
                          for(item in sortedList){
                              newList.add(item)
                          }
                          recyclerView.getLayoutManager()?.scrollToPosition(newList.size -1)
                          adapter.updateAdapter(newList)
                      }
                  }

              }
          }
       }catch (e : Exception){
           Log.d("@@RefreshChat", e.message!!)
       }
    }

    private fun sendNotification(notification : PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d("@@Notification","${Gson().toJson(response)}")
            }else{
                Log.d("@@Notification",response.errorBody().toString())
            }
        }catch (e : Exception){
            Log.d("@@Notification",e.message.toString())
        }
    }


}