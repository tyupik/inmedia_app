package ru.netology.inmedia.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.netology.inmedia.R

object AndroidUtils {

    fun View.hideKeyboard() {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}