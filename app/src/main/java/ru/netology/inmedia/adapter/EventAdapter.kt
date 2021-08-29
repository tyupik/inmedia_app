package ru.netology.inmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.inmedia.databinding.EventCardItemBinding
import ru.netology.inmedia.model.EventFeedModel
import ru.netology.inmedia.model.EventModel

class EventAdapter(
    private val listener: EventAdapterClickListener
) : PagingDataAdapter<EventFeedModel, RecyclerView.ViewHolder>(EventDiffItemCallback){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        EventViewHolder(
            EventCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> {
                val item = getItem(position) as EventModel
                holder.bind(item.event)
            }
        }
    }

    object EventDiffItemCallback : DiffUtil.ItemCallback<EventFeedModel>() {
        override fun areItemsTheSame(oldItem: EventFeedModel, newItem: EventFeedModel): Boolean {
            if (oldItem.javaClass != newItem.javaClass) {
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventFeedModel, newItem: EventFeedModel): Boolean =
            oldItem == newItem
    }
}