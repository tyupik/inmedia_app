package ru.netology.inmedia.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.adapter.PostAdapter
import ru.netology.inmedia.adapter.PostAdapterClickListener
import ru.netology.inmedia.databinding.FragmentFeedBinding
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostAdapter(
            object : PostAdapterClickListener {
                override fun onEditClicked(post: Post) {
                    TODO("Not yet implemented")
                }

                override fun onRemoveClicked(post: Post) {
                   postViewModel.removeById(post.id)
                }

                override fun onLikeClicked(post: Post) {
                    if (post.likedByMe) {
                        postViewModel.dislikeById(post.id)
                    } else {
                        postViewModel.likeById(post.id)
                    }
                }

                override fun onAttachmentClicked(post: Post) {
                    // Тут надо сделать проверку, какой тип вложения
                    // Пока что возьмем по дефолтку фото
                    findNavController().navigate(
                        R.id.action_feedFragment_to_attach_photo_viewer,
                        Bundle().apply {
                            textArg = post.attachment?.url
                        }
                    )
                }
            }
        )

//        feedViewModel =
//            ViewModelProvider(this).get(FeedViewModel::class.java)
//
//        _binding = FragmentFeedBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.emptyText
//        feedViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}