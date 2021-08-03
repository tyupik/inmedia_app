package ru.netology.inmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.inmedia.databinding.PostCardItemBinding
import ru.netology.inmedia.model.FeedModel
import ru.netology.inmedia.model.PostModel

class PostAdapter(
    private val listener: PostAdapterClickListener,
    private val url: String
) : PagingDataAdapter<FeedModel, RecyclerView.ViewHolder>(PostDiffItemCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PostViewHolder(
            PostCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener,
            url
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val item = getItem(position) as PostModel
                holder.bind(item.post)
            }
        }
    }

    object PostDiffItemCallback : DiffUtil.ItemCallback<FeedModel>() {
        override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
            if (oldItem.javaClass != newItem.javaClass) {
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean =
            oldItem == newItem
    }

}