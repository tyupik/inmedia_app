package ru.netology.inmedia.viewmodel

import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.auth.AuthState
import ru.netology.inmedia.dto.MediaUpload
import ru.netology.inmedia.model.PhotoModel
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class   AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    val postRepository: PostRepository,
) : ViewModel() {
    val data: LiveData<AuthState> = auth
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L


    fun getFileForAvatar(login: String, pass: String, name: String, photoValue: PhotoModel) = viewModelScope.launch {
        val upload = MediaUpload(photoValue.uri!!.toFile())
        val media = MultipartBody.Part.createFormData(
            "file", upload.file.name, upload.file.asRequestBody()
        )
        auth.setRegistration(login, pass, name, media)

    }


}