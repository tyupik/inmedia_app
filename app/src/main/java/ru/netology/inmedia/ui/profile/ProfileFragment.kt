package ru.netology.inmedia.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.databinding.FragmentProfileBinding
import ru.netology.inmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

//    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: AuthViewModel
    @Inject
    lateinit var auth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)


        val binding = FragmentProfileBinding.inflate(inflater, container, false)





        viewModel.data.observe(viewLifecycleOwner) {
            if(viewModel.authenticated) {
                binding.unauthenticated.visibility = View.INVISIBLE
            } else {
                binding.unauthenticated.visibility = View.VISIBLE
            }
        }


        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_fragment_login)
        }

        binding.registration.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_fragment_registration)
        }


        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}