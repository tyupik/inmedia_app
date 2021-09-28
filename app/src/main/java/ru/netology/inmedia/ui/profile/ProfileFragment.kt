package ru.netology.inmedia.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.inmedia.BuildConfig
import ru.netology.inmedia.R
import ru.netology.inmedia.adapter.PagingLoadStateAdapter
import ru.netology.inmedia.adapter.PostAdapter
import ru.netology.inmedia.adapter.PostAdapterClickListener
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.databinding.FragmentProfileBinding
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.inmedia.viewmodel.AuthViewModel
import ru.netology.inmedia.viewmodel.JobViewModel
import ru.netology.inmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var auth: AppAuth



    //    private val profileViewModel: ProfileViewModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )
    private val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val jobViewModel: JobViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostAdapter(
            object : PostAdapterClickListener {
                override fun onEditClicked(post: Post) {
                    hideNavBar()
                    findNavController().navigate(
                        R.id.action_navigation_home_to_fragment_new_post,
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
                    hideNavBar()
                    findNavController().navigate(
                        R.id.action_navigation_home_to_attach_photo_viewer,
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

        binding.swipeRefresh.setOnRefreshListener {
            postViewModel.loadUserPosts()
        }

        binding.job.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_jobs_feed
            )
        }


        postViewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.errorGroup.isVisible = false
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { postViewModel.loadUserPosts() }
                    .show()
            }
        }

        lifecycleScope.launchWhenCreated {
            postViewModel.userData.collectLatest {
                adapter.submitData(it)
            }
        }

        postViewModel.loadUserProfile(auth.getMyId())
        postViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.name.text = user.name
            Glide.with(binding.avatarIv)
                .load("${user.avatar}")
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_no_avatar_24)
                .timeout(10_000)
                .transform(MultiTransformation(FitCenter(), CircleCrop()))
                .into(binding.avatarIv)
        }

        jobViewModel.data.observe(viewLifecycleOwner, {state ->
            val jobs = state.jobs
            for (job in jobs) {
                if (job.finish == null) {
                    binding.workIn.visibility = View.VISIBLE
                    binding.workIn.text = job.name
                }
            }
        })

        binding.retryButton.setOnClickListener {
            postViewModel.loadUserPosts()
        }

        binding.addJob.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_fragment_new_job)
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