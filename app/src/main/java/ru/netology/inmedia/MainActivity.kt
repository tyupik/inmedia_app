package ru.netology.inmedia

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.databinding.ActivityMainBinding
import ru.netology.inmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.inmedia.viewmodel.AuthViewModel
import ru.netology.inmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: AuthViewModel

    private val postViewModel: PostViewModel by viewModels()

    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var firebaseInstallations: FirebaseInstallations

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment_activity_main).navigate(
                R.id.action_feedFragment_to_new_post_fragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }
        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        firebaseInstallations.id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        viewModel.data.observe(this) {
            if (viewModel.authenticated) {
                navView.menu.findItem(R.id.navigation_sign_in).isVisible = false
                navView.menu.findItem(R.id.navigation_home).isVisible = true
            } else {
                navView.menu.findItem(R.id.navigation_sign_in).isVisible = true
                navView.menu.findItem(R.id.navigation_home).isVisible = false
            }
        }



        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_sign_in,
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_events,
            )

        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        menu?.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signout -> {
                auth.removeAuth()
                postViewModel.refreshPosts()
                Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}