package com.example.connect.model

data class ChatModel(
    var chat: String? = null,
    var senderId : String? = null,
    var receiverId : String? = null,
    var date: String? = null,
    var time : Long? = null
)
