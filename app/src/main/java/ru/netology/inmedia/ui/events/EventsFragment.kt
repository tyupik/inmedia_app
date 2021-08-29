package ru.netology.inmedia.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.inmedia.R
import ru.netology.inmedia.adapter.EventAdapter
import ru.netology.inmedia.adapter.EventAdapterClickListener
import ru.netology.inmedia.adapter.PagingLoadStateAdapter
import ru.netology.inmedia.databinding.FragmentEventsBinding
import ru.netology.inmedia.dto.Event
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.inmedia.viewmodel.AuthViewModel
import ru.netology.inmedia.viewmodel.EventsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class EventsFragment : Fragment() {

    @Inject
    lateinit var viewModel: AuthViewModel

    val eventViewModel: EventsViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentEventsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = EventAdapter(
            object : EventAdapterClickListener{
                override fun onEditClicked(event: Event) {
                    hideNavBar()
                    findNavController().navigate(
                        R.id.action_navigation_events_to_fragment_new_event,
                        Bundle().apply {
                            textArg = event.content
                        }
                    )
                    eventViewModel.edit(event)
                }

                override fun onRemoveClicked(event: Event) {
                    eventViewModel.removeById(event.id)
                }

                override fun onLikeClicked(event: Event) {
                    if (event.likedByMe) {
                        eventViewModel.dislikeById(event.id)
                    } else {
                        eventViewModel.likeById(event.id)
                    }
                }

                override fun onAttachmentClicked(event: Event) {
                    hideNavBar()
                    findNavController().navigate(
                        R.id.action_navigation_events_to_attach_photo_viewer,
                        Bundle().apply {
                            textArg = event.attachment?.url
                        }
                    )
                }

                override fun onParticipateClicked(event: Event) {
                    if (event.participatedByMe) {
                        eventViewModel.unparticipateById(event.id)
                    } else {
                        eventViewModel.participateById(event.id)
                    }
                }
            }
        )

        binding.listOfEvents.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter(adapter::retry),
            footer = PagingLoadStateAdapter(adapter::retry)
        )

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        eventViewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.errorGroup.isVisible = false
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { eventViewModel.refreshEvents() }
                    .show()
            }
        })

        lifecycleScope.launchWhenCreated {
            eventViewModel.data.collectLatest {
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
            eventViewModel.refreshEvents()
        }

        viewModel.data.observe(viewLifecycleOwner) {
            if(viewModel.authenticated) {
                binding.fab.visibility = View.VISIBLE
            } else {
                binding.fab.visibility = View.INVISIBLE
            }
        }

        binding.listOfEvents.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        binding.fab.setOnClickListener {
            hideNavBar()
            findNavController().navigate(R.id.action_navigation_events_to_fragment_new_event)
        }
        return binding.root
    }

    private fun hideNavBar() {
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE
    }

}