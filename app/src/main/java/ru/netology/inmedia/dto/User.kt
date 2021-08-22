package ru.netology.inmedia.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
    val authorities: List<String>,
)