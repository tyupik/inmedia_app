package ru.netology.inmedia.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.databinding.FragmentRegistrationBinding
import ru.netology.inmedia.model.PhotoModel
import ru.netology.inmedia.ui.profile.ProfileViewModel
import ru.netology.inmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.inmedia.viewmodel.AuthViewModel
import javax.inject.Inject


private var photoValue = PhotoModel(null)

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var authViewModel: AuthViewModel

    private val profileViewModel: ProfileViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    private val photoRequestCode = 1
    private val cameraRequestCode = 2
    private var fragmentBinding: FragmentRegistrationBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegistrationBinding.inflate(
            inflater,
            container,
            false
        )

//        val login = binding.username.toString()
//        val pass = binding.password.toString()
//        val name = binding.name.toString()
        fragmentBinding = binding



        binding.pickAvatar.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val animals = arrayOf(
                getString(R.string.description_select_photo),
                getString(R.string.description_take_photo)
            )
            builder.setItems(animals) { _, which ->
                when (which) {
                    0 -> {
                        ImagePicker.with(this)
                            .crop()
                            .compress(2048)
                            .galleryOnly()
                            .galleryMimeTypes(
                                arrayOf(
                                    "image/png",
                                    "image/jpeg",
                                )
                            )
                            .start(photoRequestCode)
                    }
                    1 -> {
                        ImagePicker.with(this)
                            .crop()
                            .compress(2048)
                            .cameraOnly()
                            .start(cameraRequestCode)
                    }
                }
            }
            val dialog = builder.create()
            dialog.show()
        }


        binding.registration.setOnClickListener {
            val login = binding.username.text.toString()
            val pass = binding.password.text.toString()
            val name = binding.name.text.toString()
            if (photoValue != PhotoModel(null)) {
                authViewModel.getFileForAvatar(login, pass, name, photoValue)
            } else {
                appAuth.setRegistration(login, pass, name, null)
            }
        }

        profileViewModel.photo.observe(viewLifecycleOwner) {
            Glide.with(binding.pickAvatar)
                .load(it.uri)
                .placeholder(R.drawable.ic_add_an_avatar_96)
                .timeout(10_000)
                .transform(MultiTransformation(FitCenter(), CircleCrop()))
                .into(binding.pickAvatar)
        }

        binding.removeAvatar.setOnClickListener {
            profileViewModel.changePhoto(null)
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if(authViewModel.authenticated) {
                requireView().hideKeyboard()
                findNavController().navigate(R.id.navigation_dashboard)
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
//            val file: File? = ImagePicker.getFile(data)
            profileViewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
//            val file: File? = ImagePicker.getFile(data)
            profileViewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
            return
        }
    }
}