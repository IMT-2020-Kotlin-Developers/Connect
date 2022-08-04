package com.example.connect.model

data class PostModel (
    var uid:String? = null,
    var imageUrl:String?= null,
    var description:String?=null,
    var fullName : String? = null,
    var photoProfileUrl : String? = null,
    var likes : ArrayList<LikeModel> = ArrayList(),
    var comments: ArrayList<CommentModel> = ArrayList(),
    var time:Long?=null
        )
