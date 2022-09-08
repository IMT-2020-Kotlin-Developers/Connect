package com.example.connect.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.connect.R
import com.example.connect.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {
   lateinit var binding : FragmentNotificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater , container , false)
        return binding.root
    }


}