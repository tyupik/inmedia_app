package ru.netology.inmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.inmedia.dto.Job

@Entity
class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val position: String,
    val start: Long,
    val finish: Long? = null
) {
    fun toDto() =
        Job(
            id = id,
            name = name,
            position = position,
            start = start,
            finish = finish,
        )

    companion object {
        fun fromDto(dto: Job) =
            JobEntity(
                id = dto.id,
                name = dto.name,
                position = dto.position,
                start = dto.start,
                finish = dto.finish
            )
    }
}

fun List<Job>.toEntity(): List<JobEntity> = map(JobEntity.Companion::fromDto)
fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)