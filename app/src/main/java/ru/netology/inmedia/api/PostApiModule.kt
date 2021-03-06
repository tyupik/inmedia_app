package ru.netology.inmedia.api

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.inmedia.BuildConfig
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.repository.PostRepository
import ru.netology.inmedia.viewmodel.AuthViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PostApiModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/"
    }

    @Provides
    fun provideAuthPrefs(
        @ApplicationContext
        context: Context
    ): SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideOkhttp(
        authPrefs: SharedPreferences
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        })
        .addInterceptor { chain ->
            authPrefs.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            }
            chain.proceed(chain.request())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    @Singleton
    @Provides
    fun providePostApi(retrofit: Retrofit):PostApiService = retrofit.create()

    @Singleton
    @Provides
    fun provideAuth(
        @ApplicationContext
        context: Context,
        postRepository: PostRepository,
        postApiService: PostApiService,
    ): AppAuth = AppAuth(context, postRepository, postApiService)


    @Singleton
    @Provides
    fun provideAuthViewModel(
        appAuth: AppAuth,
        postRepository: PostRepository
    ) : AuthViewModel = AuthViewModel(appAuth, postRepository)

    @Singleton
    @Provides
    fun provideProfileApi(retrofit: Retrofit):ProfileApiService = retrofit.create()

    @Singleton
    @Provides
    fun provideEventApi(retrofit: Retrofit):EventApiService = retrofit.create()

    @Singleton
    @Provides
    fun provideJobApi(retrofit: Retrofit): JobApiService = retrofit.create()
}