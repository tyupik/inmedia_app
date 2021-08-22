package ru.netology.inmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentProfileBinding
import ru.netology.inmedia.databinding.PostCardItemBinding
import ru.netology.inmedia.model.FeedModel
import ru.netology.inmedia.model.PostModel
import ru.netology.inmedia.model.ProfileModel
import ru.netology.inmedia.model.UserModel

class UserAdapter(
    private val listener: PostAdapterClickListener,
    private val url: String
) : PagingDataAdapter<FeedModel, RecyclerView.ViewHolder>(PostDiffItemCallback) {


    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ProfileModel -> R.layout.fragment_profile
            is PostModel -> R.layout.post_card_item
            null -> error("Unknown type at $position")
            else -> 0
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.post_card_item -> {
                val binding = PostCardItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PostViewHolder(binding, listener, url)
            }
            R.layout.fragment_profile -> {
                val binding = FragmentProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ProfileViewHolder(binding)
            }
            else -> error("Unknown view type $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProfileViewHolder -> {
                val item = getItem(position) as UserModel
                holder.bindUser(item.user)
            }
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