package ru.netology.inmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )


        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_sign_in_to_fragment_login)
        }

        binding.registration.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_sign_in_to_fragment_registration)
        }


        return binding.root
    }
}