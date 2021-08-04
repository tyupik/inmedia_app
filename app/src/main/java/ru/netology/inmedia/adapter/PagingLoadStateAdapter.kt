package ru.netology.inmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.inmedia.databinding.ItemLoadStateBinding

class PagingLoadStateAdapter(private val onRetryListener: () -> Unit) :
    LoadStateAdapter<PagingLoadStateAdapter.PagingLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PagingLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PagingLoadStateViewHolder {
        val binding =
            ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagingLoadStateViewHolder(binding, onRetryListener)
    }

    class PagingLoadStateViewHolder(
        private val binding: ItemLoadStateBinding,
        private val onRetryListener: () -> Unit
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(state: LoadState) {
            with(binding) {
                retryButton.isVisible = state is LoadState.Error
                errorText.isVisible = state is LoadState.Error
                progress.isVisible = state is LoadState.Loading
                retryButton.setOnClickListener { onRetryListener() }
            }
        }
    }
}