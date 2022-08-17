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
import com.example.connect.viewModel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    var user = UserModel()
    lateinit var viewModel : ChatViewModel
    lateinit var auth : FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : ChatItemAdapter
    var chat = ChatModel()
    var listOfChat = ArrayList<ChatModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        recyclerView = binding.chatRecycleView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

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

}