package ru.netology.inmedia.adapter

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.inmedia.R
import ru.netology.inmedia.databinding.JobCardItemBinding
import ru.netology.inmedia.dto.Job
import java.text.SimpleDateFormat
import java.util.*

class JobViewHolder(
    private val binding: JobCardItemBinding,
    private val listener: JobAdapterClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            jobStart.text = convertDate(job.start)
            jobFinish.text = if(job.finish != null) convertDate(job.finish) else "н/в"
            position.text = job.position
            companyName.text = job.name
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemoveClicked(job)
                                true
                            }
                            R.id.edit -> {
                                listener.onEditClicked(job)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }

    private fun convertDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MM.yyyy")
        return format.format(date)
    }
}