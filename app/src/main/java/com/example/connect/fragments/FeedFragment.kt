package com.example.connect.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.adapter.ProfilePostAdapter
import com.example.connect.adapter.UserListAdapter
import com.example.connect.databinding.FragmentFeedBinding
import com.example.connect.model.UserModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class FeedFragment : Fragment() {

    lateinit var binding : FragmentFeedBinding
     private var userList  = ArrayList<UserModel>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: UserListAdapter
    var db = Firebase.firestore.collection("User")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(layoutInflater,container,false)
        userList = ArrayList()
        recyclerView = binding.feedRecycleView
        recyclerView.layoutManager = LinearLayoutManager(context)

            db.get().addOnSuccessListener{
                val list : List<DocumentSnapshot> = it.documents
                for(user in list){
                    val User : UserModel? = user.toObject(UserModel::class.java)
                    if(User != null){
                        userList.add(User)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            val tempArrayList = ArrayList<UserModel>()
            binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                  return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                 tempArrayList.clear()
                    val searchText = p0!!.lowercase(Locale.getDefault())
                    if(searchText.isNotEmpty()){
                        userList.forEach {
                            if(it.fullName?.lowercase(Locale.getDefault())?.contains(searchText)!!){

                                tempArrayList.add(it)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }else{
                        tempArrayList.clear()
                        adapter.notifyDataSetChanged()
                    }
                    return false
                }
            })
            adapter = UserListAdapter(tempArrayList)
            recyclerView.adapter = adapter


        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
             if(binding.svSearch.visibility == View.VISIBLE){
                 binding.svSearch.visibility = View.GONE
                 binding.feedRecycleView.visibility = View.GONE
//                 binding.ivSearch.visibility = View.VISIBLE

             }else{
                 activity!!.finish()
             }

            }
        })


        return binding.root
    }



}
