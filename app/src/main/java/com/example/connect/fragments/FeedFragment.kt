package com.example.connect.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.connect.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    lateinit var binding : FragmentFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(layoutInflater,container,false)

//        binding.bottomNavView.background = null
//        binding.bottomNavView.menu.getItem(2).isEnabled = false

//        binding.bottomNavView.setOnItemSelectedListener {
//            when(it.itemId){
//                R.id.bnProfile -> {
//
//                    return@setOnItemSelectedListener true
//                }
//
//            }
//            true
//        }


        return binding.root
    }


}
