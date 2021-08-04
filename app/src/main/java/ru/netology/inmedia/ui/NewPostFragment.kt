package ru.netology.inmedia.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentNewPostBinding
import ru.netology.inmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.inmedia.viewmodel.PostViewModel
import java.io.File


@AndroidEntryPoint
class NewPostFragment :Fragment() {
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

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    viewModel.changeContent(it.edit.text.toString())
                    viewModel.save()
                    requireView().hideKeyboard()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String?
            set(value) = putString(TEXT_KEY, value)
            get() = getString(TEXT_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg?.let (binding.edit::setText)

        fragmentBinding = binding

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(arrayOf(
                    "image/png",
                    "image/jpeg",
                ))
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
            viewModel.changePhoto(null, null)
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
    }
}