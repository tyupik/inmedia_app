package ru.netology.inmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.BuildConfig
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentAttachPhotoViewerBinding
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg

@AndroidEntryPoint
class AttachViewerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    val BASE_URL = "${BuildConfig.BASE_URL}"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val binding = FragmentAttachPhotoViewerBinding.inflate(
            inflater,
            container,
            false
        )



        Glide.with(binding.attach)
            .load("$BASE_URL/media/${arguments?.textArg}")
            .error(R.drawable.ic_error_100dp)
            .timeout(10_000)
            .into(binding.attach)
        return binding.root
    }

}