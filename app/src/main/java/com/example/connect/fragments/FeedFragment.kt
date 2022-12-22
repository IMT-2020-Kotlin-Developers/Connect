package com.example.connect.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.activities.MainActivity
import com.example.connect.adapter.FeedPostAdapter
import com.example.connect.adapter.ProfilePostAdapter
import com.example.connect.adapter.UserListAdapter
import com.example.connect.databinding.FragmentFeedBinding
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.example.connect.viewModel.FireBaseViewModel
import com.example.connect.viewModel.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import android.widget.AbsListView




class FeedFragment : Fragment() {

    lateinit var binding: FragmentFeedBinding
    private var userList = ArrayList<UserModel>()
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewPosts: RecyclerView
    lateinit var adapter: UserListAdapter
    lateinit var adapterFeedProfile: FeedPostAdapter
    lateinit var viewModel : PostViewModel
    var db = Firebase.firestore.collection("User")
    var dbPost = Firebase.firestore.collection("Posts")
    lateinit var auth: FirebaseAuth
    private var itemList = ArrayList<PostModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        userList = ArrayList()
        recyclerView = binding.feedRecycleView
        recyclerViewPosts = binding.postRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewPosts.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProvider(activity!!)[PostViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        db.get().addOnSuccessListener {
            val list: List<DocumentSnapshot> = it.documents
            for (user in list) {
                val User: UserModel? = user.toObject(UserModel::class.java)
                if (User != null) {
                    userList.add(User)
                }
            }
        }

        val tempArrayList = ArrayList<UserModel>()
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                tempArrayList.clear()
                val searchText = p0!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    userList.forEach {
                        if (it.fullName?.lowercase(Locale.getDefault())?.contains(searchText)!! && it.uid != auth.uid) {

                            tempArrayList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    tempArrayList.clear()
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })
        adapter = UserListAdapter(tempArrayList)
        viewModel.getPosts()


        viewModel.posts().observe(viewLifecycleOwner,Observer{
            itemList = it
//            adapterProfile.notifyItemInserted(itemList.size - 1)
//            adapterProfile.notifyDataSetChanged()
            adapterFeedProfile.updateAdapter(itemList)


            Log.d("@@ObserveInfeed ",itemList.toString())
        })

        adapterFeedProfile = FeedPostAdapter(itemList)
//        itemList.add(PostModel("v0dBqhi8QIeUkASWiYtE","https://firebasestorage.googleapis.com/v0/b/connect-f04dd.appspot.com/o/I6pSv0IX9CesEhs1dx9CXexvVaE2%2FprofilePic?alt=media&token=6d5b7ea3-55e0-49f1-9693-049f00e3ccb6",
//        "Hehe",111651654))
        recyclerViewPosts.adapter = adapterFeedProfile

//        recyclerViewPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0) {
//                    if(binding.svSearch.visibility != View.GONE){
//                        binding.svSearch.visibility = View.GONE
//                    }
//                } else if (dy < 0) {
//                    if(binding.svSearch.visibility != View.VISIBLE){
//                        binding.svSearch.visibility = View.VISIBLE
//                    }
//                }
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                when (newState) {
//                    AbsListView.OnScrollListener.SCROLL_STATE_FLING -> println("SCROLL_STATE_FLING")
//                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> binding.svSearch.visibility = View.GONE
//                    else -> {
//                    }
//                }
//            }
//        })
        recyclerView.adapter = adapter
        Log.d("@@Check For Feed ",itemList.toString())
//        adapterProfile.notifyItemInserted(itemList.size - 1)
//        adapterProfile.updateAdapter(itemList)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!binding.svSearch.isIconified) {
                    binding.svSearch.isIconified = true
                } else {
                    activity!!.finish()
                }

            }
        })




        return binding.root
    }


}
