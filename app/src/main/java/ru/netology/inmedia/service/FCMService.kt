package ru.netology.inmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.inmedia.R
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.auth.RecipientInfo
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService() : FirebaseMessagingService() {
    private val channelId = "remote"
    private val gson = Gson()

    @Inject
    lateinit var auth: AppAuth

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val msg = gson.fromJson(message.data["content"], RecipientInfo::class.java)

        val myid = auth.authStateFlow.value.id

        if (msg.recipientId == null) {
            //show notify
            notification(this, msg.content)
        }

        if (msg.recipientId == myid.toString()) {
            //show notify
            notification(this, msg.content)
        }

        if (msg.recipientId == "0" && msg.recipientId != myid.toString()) {
            //send again
            auth.sendPushToken()
        }

        if (msg.recipientId != "0" && msg.recipientId != myid.toString()) {
            //send again
            auth.sendPushToken()
        }
    }

    override fun onNewToken(token: String) {
        auth.sendPushToken(token)
        println(token)
    }

    private fun notification(context: Context, msg: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.avatar)
            .setContentTitle(getString(R.string.new_message))
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}