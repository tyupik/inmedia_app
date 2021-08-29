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
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import ru.netology.inmedia.viewmodel.AuthViewModel
import ru.netology.inmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {
    @Inject
    lateinit var viewModel: AuthViewModel

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
                    hideNavBar()
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
                    // Пока что возьмем по дефолту фото
                    hideNavBar()
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
                    .setAction(R.string.retry_loading) { postViewModel.refreshPosts() }
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

        viewModel.data.observe(viewLifecycleOwner) {
            if(viewModel.authenticated) {
                binding.fab.visibility = View.VISIBLE
            } else {
                binding.fab.visibility = View.INVISIBLE
            }
        }

        binding.listOfPosts.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        binding.fab.setOnClickListener {
            hideNavBar()
            findNavController().navigate(R.id.action_feedFragment_to_new_post_fragment)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
    }

    private fun hideNavBar() {
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE
    }
}