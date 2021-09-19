package ru.netology.inmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.adapter.JobAdapter
import ru.netology.inmedia.adapter.JobAdapterClickListener
import ru.netology.inmedia.databinding.FragmentJobsFeedBinding
import ru.netology.inmedia.dto.Job
import ru.netology.inmedia.ui.NewJobFragment.Companion.finishArg
import ru.netology.inmedia.ui.NewJobFragment.Companion.nameArg
import ru.netology.inmedia.ui.NewJobFragment.Companion.positionArg
import ru.netology.inmedia.ui.NewJobFragment.Companion.startArg
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.inmedia.viewmodel.JobViewModel

@AndroidEntryPoint
class JobFragment : Fragment() {

    private val jobViewModel: JobViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentJobsFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = JobAdapter(
            object : JobAdapterClickListener{
                override fun onEditClicked(job: Job) {
                    findNavController().navigate(
                        R.id.action_navigation_jobs_feed_to_fragment_new_job,
                        Bundle().apply {
                            startArg = job.start
                            if (job.finish != null) finishArg = job.finish else 0L
                            nameArg = job.name
                            positionArg = job.position
                        }
                    )
                    jobViewModel.edit(job)
                }

                override fun onRemoveClicked(job: Job) {
                    jobViewModel.removeJobById(job.id)
                }
            }
        )

        binding.listOfJobs.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            jobViewModel.refreshJobs()
        }

        jobViewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.errorGroup.isVisible = false
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { jobViewModel.refreshJobs() }
                    .show()
            }
        })

        jobViewModel.data.observe(viewLifecycleOwner, {state ->
            binding.emptyText.isVisible = state.empty
            adapter.submitList(state.jobs)

        })

        binding.retryButton.setOnClickListener {
            jobViewModel.refreshJobs()
        }

        binding.listOfJobs.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_jobs_feed_to_fragment_new_job)
        }



        return binding.root
    }
}