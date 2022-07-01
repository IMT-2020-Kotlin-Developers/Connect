package com.example.connect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.connect.databinding.FragmentIntroScreenBinding

class IntroScreenFragment : Fragment() {

    lateinit var binding: FragmentIntroScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentIntroScreenBinding.inflate(layoutInflater, container, false)
        binding.btLogIn.setOnClickListener{

        }
        binding.btSignIn.setOnClickListener{
            findNavController().navigate(R.id.action_introScreenFragment_to_signInFragment)
        }
        binding.btLogIn.setOnClickListener{
            findNavController().navigate(R.id.action_introScreenFragment_to_logInFragment)
        }
        return binding.root
    }

}