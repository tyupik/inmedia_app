package ru.netology.inmedia.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.*
import com.google.firebase.installations.FirebaseInstallations
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.inmedia.SingleLiveEvent
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.dao.PostDao
import ru.netology.inmedia.dto.*
import ru.netology.inmedia.model.FeedModel
import ru.netology.inmedia.model.FeedModelState
import ru.netology.inmedia.model.PhotoModel
import ru.netology.inmedia.model.PostModel
import ru.netology.inmedia.repository.PostRepository
import ru.netology.inmedia.repository.ProfileRepository
import ru.netology.inmedia.work.RemovePostWorker
import ru.netology.inmedia.work.SavePostWorker
import java.io.File
import java.lang.Exception
import java.time.Instant
import javax.inject.Inject

private val defaultPost = Post(
    id = 0L,
    authorId = 0L,
    author = "",
    authorAvatar = "1",
    content = "",
    published = "",
)
private val defaultUser = User(
    id = 0L,
    login = "",
    name = "",
    authorities = emptyList()
)

private val noPhoto = PhotoModel()

@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val workManager: WorkManager,
    auth: AppAuth,
    private val profileRepository: ProfileRepository
) : ViewModel() {


    private val cached = postRepository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedModel>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    PostModel(post.copy(ownedByMe = post.authorId == myId))
                }
            }
        }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(defaultPost)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _user = MutableLiveData(defaultUser)
    val user: MutableLiveData<User>
        get() = _user

    private val _userPosts = MutableLiveData<List<Post>>()
    val userPost: LiveData<List<Post>>
    get() = _userPosts


    init {
        loadPosts()
        loadUserPosts()
        FirebaseInstallations.getInstance().getToken(true)
    }

    fun loadUserProfile(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            val us = profileRepository.getUserById(id)
            user.value = user.value?.copy(id = us.id, login = us.login, name = us.name)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            postRepository.getLatestPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun loadUserPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            profileRepository.getLatestPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            postRepository.getLatestPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.likeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.dislikeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        viewModelScope.launch {
            val data = workDataOf(RemovePostWorker.postKey to id)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<RemovePostWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(request)
        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (text == edited.value?.content) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun save(photoValue: PhotoModel) = viewModelScope.launch {
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = postRepository.saveWork(
                        post, photoValue.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SavePostWorker.postKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SavePostWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = defaultPost
        _photo.value = noPhoto
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }
}