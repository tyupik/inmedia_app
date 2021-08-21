package ru.netology.inmedia.adapter

import android.app.Application
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.PostCardItemBinding
import ru.netology.inmedia.dto.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PostViewHolder(
    private val binding: PostCardItemBinding,
    private val listener: PostAdapterClickListener,
    private val url: String
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            avatarIv.setImageResource((R.drawable.avatar))
            authorTv.text = post.author
            publishedTv.text = formatData(post.published)
            textTv.text = post.content
            like.isChecked = post.likedByMe
            menu.visibility = if(post.ownedByMe) View.VISIBLE else View.INVISIBLE

            like.setOnClickListener {
                listener.onLikeClicked(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemoveClicked(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEditClicked(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            attachment.setOnClickListener  {
                listener.onAttachmentClicked(post)
            }

            Glide.with(binding.avatarIv)
                .load("${post.authorAvatar}")
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .timeout(10_000)
                .transform(MultiTransformation(FitCenter(), CircleCrop()))
                .into(binding.avatarIv)


            if(!post.attachment?.url.isNullOrEmpty()) {
                binding.attachment.visibility = View.VISIBLE
                Glide.with(binding.attachment)
                    .load("${post.attachment?.url}")
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
        return DateTimeFormatter.ofPattern("d.MM.yyyy 'в' HH:mm ")
            .withZone(ZoneId.of("Europe/Moscow"))
            .format(Instant.parse(instant))
    }

}