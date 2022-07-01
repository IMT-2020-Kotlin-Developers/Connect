package com.example.connect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.connect.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    lateinit var binding : FragmentFeedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(layoutInflater,container,false)
        binding.bottomNavView.background = null
        binding.bottomNavView.menu.getItem(2).isEnabled = false
        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bnProfile -> {
                    findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
                    return@setOnItemSelectedListener true
                }

            }
            false
        }
        return binding.root
    }


    }
