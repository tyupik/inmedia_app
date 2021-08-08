package ru.netology.inmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.databinding.FragmentLoginBinding
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor(
    private val appAuth: AppAuth
): Fragment() {
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

        val login = binding.username.toString()
        val pass = binding.password.toString()

        //Нужно понять, откуда взять токен?!
        lateinit var token: String


        binding.loading.setOnClickListener {
            appAuth.setAuthTyupik(login, pass, token)
        }
        return binding.root
    }

}