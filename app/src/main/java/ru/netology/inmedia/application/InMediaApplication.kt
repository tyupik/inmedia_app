package ru.netology.inmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.inmedia.auth.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class InMediaApplication : Application() {

    @Inject
    lateinit var auth: AppAuth
    override fun onCreate() {
        super.onCreate()
    }
}