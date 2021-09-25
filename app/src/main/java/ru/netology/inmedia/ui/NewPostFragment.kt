package ru.netology.inmedia.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentNewPostBinding
import ru.netology.inmedia.model.FeedModelState
import ru.netology.inmedia.model.PhotoModel
import ru.netology.inmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.inmedia.viewmodel.PostViewModel
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import java.net.URL
import java.net.URLConnection
import android.provider.MediaStore.Images
import java.io.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val photoRequestCode = 1
    private val cameraRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_post_menu, menu)
    }

    private var fragmentBinding: FragmentNewPostBinding? = null
    private var photoValue = PhotoModel(null)


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if (fragmentBinding?.edit?.text?.isBlank() == true) {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_empty_content,
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
                fragmentBinding?.let {
                    viewModel.changeContent(it.edit.text.toString())
                    viewModel.save(photoValue)
                    requireView().hideKeyboard()
                    if (viewModel.dataState.value == FeedModelState(loading = true)) {
                        findNavController().navigateUp()
                    } else {
                        print(viewModel.dataState.value)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TEXT_KEY = "TEXT_KEY"
        private const val PHOTO_KEY = "PHOTO_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
        var Bundle.photoArg: String?
            set(value) = putString(PHOTO_KEY, value)
            get() = getString(PHOTO_KEY)
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        viewModel.changePhoto(null)
        photoValue = PhotoModel(null)

        arguments?.textArg?.let(binding.edit::setText)

        arguments?.photoArg?.let { url->
            val uri = Uri.parse(url)
            viewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
        }

        fragmentBinding = binding

        binding.pickPhoto.setOnClickListener {
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

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null)
        }
        viewModel.photo.observe(viewLifecycleOwner) {


            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
            Glide.with(binding.photo)
                .load("${it.uri}")
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .into(binding.photo)

        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
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
            viewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            viewModel.changePhoto(uri)
            photoValue = PhotoModel(uri)
            return
        }
    }
}
