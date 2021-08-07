package ru.netology.inmedia.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.inmedia.BuildConfig
import ru.netology.inmedia.R
import ru.netology.inmedia.adapter.PagingLoadStateAdapter
import ru.netology.inmedia.adapter.PostAdapter
import ru.netology.inmedia.adapter.PostAdapterClickListener
import ru.netology.inmedia.databinding.FragmentFeedBinding
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.inmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


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
                    findNavController().navigate(
                        R.id.action_feedFragment_to_new_post_fragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
                    postViewModel.edit(post)
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
            },
            "${BuildConfig.BASE_URL}"
        )

        binding.listOfPosts.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        postViewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.errorGroup.isVisible = false
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { postViewModel.loadPosts() }
                    .show()
            }
        })

        lifecycleScope.launchWhenCreated {
            postViewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { states ->
                binding.swipeRefresh.isRefreshing =
                    states.refresh is LoadState.Loading
            }
        }

        binding.retryButton.setOnClickListener {
            postViewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_new_post_fragment)
        }
        return binding.root
    }
}