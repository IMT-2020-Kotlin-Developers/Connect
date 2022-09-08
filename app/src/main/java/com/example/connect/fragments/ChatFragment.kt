package com.example.connect.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connect.R
import com.example.connect.adapter.ChatAdapter
import com.example.connect.adapter.UserListAdapter
import com.example.connect.databinding.FragmentChatBinding
import com.example.connect.model.PostModel
import com.example.connect.model.UserModel
import com.example.connect.viewModel.ChatViewModel
import com.example.connect.viewModel.PostViewModel


class ChatFragment : Fragment() {

    lateinit var binding : FragmentChatBinding
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : ChatAdapter
    lateinit var viewModel : ChatViewModel
    private var itemList = ArrayList<UserModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater , container , false)
        recyclerView = binding.chatUserRecycleView
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel = ViewModelProvider(activity!!)[ChatViewModel::class.java]

        viewModel.following().observe(viewLifecycleOwner , Observer {
            itemList =  it
            adapter.updateAdapter(itemList)
        })
        adapter = ChatAdapter(itemList)
        recyclerView.adapter = adapter
        return binding.root
    }

}