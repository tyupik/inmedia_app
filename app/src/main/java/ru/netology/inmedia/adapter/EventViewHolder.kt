package ru.netology.inmedia.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.EventCardItemBinding
import ru.netology.inmedia.dto.Event
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EventViewHolder (
    private val binding: EventCardItemBinding,
    private val listener: EventAdapterClickListener,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(event: Event) {
        binding.apply {
            avatarIv.setImageResource(R.drawable.avatar)
            authorTv.text = event.author
            dateTimeTV.text = formatData(event.datetime)
            eventTypeTV.text = event.type.name
            contentTV.text = event.content
            publishedTV.text = formatData(event.published)
            participateMB.text = if (event.participatedByMe) "Не принимать участие" else "Принять участие"
            menu.visibility = if(event.ownedByMe) View.VISIBLE else View.INVISIBLE
            numberOfParticipants.text = event.participateCount.toString()
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, event.ownedByMe)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemoveClicked(event)
                                true
                            }
                            R.id.edit -> {
                                listener.onEditClicked(event)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }


            participateMB.setOnClickListener {
                listener.onParticipateClicked(event)
            }

            attachment.setOnClickListener  {
                listener.onAttachmentClicked(event)
            }

            Glide.with(binding.avatarIv)
                .load("${event.authorAvatar}")
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_no_avatar_24)
                .timeout(10_000)
                .transform(MultiTransformation(FitCenter(), CircleCrop()))
                .into(binding.avatarIv)

            if(!event.attachment?.url.isNullOrEmpty()) {
                binding.attachment.visibility = View.VISIBLE
                Glide.with(binding.attachment)
                    .load("${event.attachment?.url}")
                    .override(1000,500)
                    .centerCrop()
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .into(binding.attachment)
            } else {
                binding.attachment.visibility = View.GONE
            }

        }
    }

    private fun formatData(instant: String) : String {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy 'в' HH:mm ")
            .withZone(ZoneId.of("Europe/Moscow"))
            .format(Instant.parse(instant))
    }
}