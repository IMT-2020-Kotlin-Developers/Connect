package com.example.connect.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connect.model.CommentModel
import com.example.connect.model.LikeModel
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostViewModel(application: Application) : AndroidViewModel(application)  {
    var db = Firebase.firestore.collection("User")
    var dbPost = Firebase.firestore.collection("Posts")
    var auth  : FirebaseAuth = FirebaseAuth.getInstance()
    private var currentPosts = MutableLiveData<ArrayList<PostModel>>()
    fun posts() : MutableLiveData<ArrayList<PostModel>> {
        return currentPosts
    }

    fun getPosts() = CoroutineScope(Dispatchers.IO).launch{
        var listOfFollowers = ArrayList<String>()
        var itemList = ArrayList<PostModel>()
        val postList = ArrayList<PostModel>()
        db.get().addOnSuccessListener {
            val list: List<DocumentSnapshot> = it.documents
            for (currUser in list) {

                val User: UserModel? = currUser.toObject(UserModel::class.java)
                if (User?.uid == auth.currentUser?.uid) {
                    listOfFollowers = User?.following!!
                    itemList = ArrayList()
                    Log.d("followingSize", listOfFollowers.size.toString())
                    for (Following in listOfFollowers) {
                        dbPost.whereEqualTo("uid", Following).get().addOnSuccessListener {
                            val list: List<DocumentSnapshot> = it.documents
                            Log.d("Fetching", list.toString())
                            for (data in list) {
                                val post: PostModel? = data.toObject(PostModel::class.java)
                                if (post != null) {
                                    Log.d("@@User", post.uid.toString())

                                    postList.add(post)

                                    Log.d("@@post", "${currentPosts}")
                                } else {
                                    Log.d("Post", "Null")
                                }

                            }
                            currentPosts.postValue(postList)
                        }
                    }

                    break
                }
            }

        }


//        dbPost.get().addOnSuccessListener {
//            val list: List<DocumentSnapshot> = it.documents
//            Log.d("Fetching", list.toString())
//            for (data in list) {
//                val post: PostModel? = data.toObject(PostModel::class.java)
//                if (post != null) {
//                    Log.d("@@User", post.uid.toString())
//
//                    postList.add(post)
//
//                    Log.d("@@post", "${currentPosts}")
//                } else {
//                    Log.d("Post", "Null")
//                }
//
//            }
//            currentPosts.postValue(postList)
//        }

    }

    fun updateLikeAndCommentPost(uid : String,likes: Int, comments : Int)  = CoroutineScope(Dispatchers.IO).launch{
        val personQuery = dbPost.whereEqualTo("uid",uid).get().await()
        var post = PostModel()

        if (personQuery.documents.isNotEmpty()) {
            try {
                for (Doc in personQuery) {
                    post = dbPost.document(Doc.id).get() as PostModel
                    var likes = LikeModel(likes,uid,"","")
                    post.likes.add(likes)
                    dbPost.document(Doc.id).set(post, SetOptions.merge()).await()
                    Log.d("@@ulacp", "${post}")
                }

            } catch (e: Exception) {
            }
        }
    }


}