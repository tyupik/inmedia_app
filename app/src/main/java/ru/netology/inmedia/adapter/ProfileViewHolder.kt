package ru.netology.inmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.FragmentProfileBinding
import ru.netology.inmedia.dto.User

class ProfileViewHolder(
    private val binding: FragmentProfileBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bindUser(user: User) {
        binding.apply {
            avatarIv.setImageResource((R.drawable.avatar))
            name.text = user.name
        }

        Glide.with(binding.avatarIv)
            .load("${user.avatar}")
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.ic_no_avatar_24)
            .timeout(10_000)
            .transform(MultiTransformation(FitCenter(), CircleCrop()))
            .into(binding.avatarIv)
    }
}