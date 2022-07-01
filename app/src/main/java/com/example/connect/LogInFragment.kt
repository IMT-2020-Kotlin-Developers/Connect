package com.example.connect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.connect.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    lateinit var binding: FragmentLogInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(layoutInflater, container, false)
        binding.btLogIn.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_feedFragment)
        }
        return binding.root

    }

}