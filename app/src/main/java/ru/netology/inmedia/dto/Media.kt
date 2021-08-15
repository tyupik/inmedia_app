package ru.netology.inmedia.dto

import java.io.File

data class Media(val url: String)

data class MediaUpload(val file: File)