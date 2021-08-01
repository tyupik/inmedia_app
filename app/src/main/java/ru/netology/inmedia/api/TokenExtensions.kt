package ru.netology.inmedia.api

import android.content.SharedPreferences
import androidx.core.content.edit

private const val KEY = "token"
var SharedPreferences.token: String?
    get() = getString(KEY, null)
    set(value) {
        edit { putString(KEY, value) }
    }