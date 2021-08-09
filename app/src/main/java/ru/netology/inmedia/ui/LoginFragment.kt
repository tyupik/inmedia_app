package ru.netology.inmedia.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.api.token
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.databinding.FragmentLoginBinding
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
//    private val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val binding = FragmentLoginBinding.inflate(
           inflater,
           container,
           false
       )

        binding.login.setOnClickListener {
            val login = binding.username.text.toString()
            val pass = binding.password.text.toString()
            appAuth.setAuthTest(login, pass)
        }
        return binding.root
    }

}