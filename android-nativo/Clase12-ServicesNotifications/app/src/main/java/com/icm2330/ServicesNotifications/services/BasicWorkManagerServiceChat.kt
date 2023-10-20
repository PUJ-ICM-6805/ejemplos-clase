package com.icm2330.ServicesNotifications.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.icm2330.ServicesNotifications.ChatActivity
import com.icm2330.ServicesNotifications.R
import com.icm2330.ServicesNotifications.model.DatabasePaths


class BasicWorkManagerServiceChat (appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    val TAG = BasicWorkManagerService::class.java.name

    val CHANNEL_ID = "MyApp"

    // Variables for Firebase DB
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference

    var firstLoad = false

    override suspend fun doWork(): Result {

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        createNotificationChannel()
        loadMessagesNotifications()

        return Result.success()
    }


    fun loadMessagesNotifications() {

        // Notification builder
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        mBuilder.setContentTitle("New Message")
            .setSmallIcon(R.drawable.chat_icon)
            .setContentText("You have a new message in the chat activity.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Action associated to notification
        val intent = Intent(applicationContext, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Set the intent that fires when the user taps the notification.
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setAutoCancel(true) // Removes notification when touching it

        firstLoad = true
        myRef = database.getReference(DatabasePaths.CHAT)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!firstLoad) {
                    // Throw notification
                    val notificationId = 10
                    val notificationManager = NotificationManagerCompat.from(applicationContext)

                    // NotificationId es un entero único definido para cada notificatión que se lanza
                    if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notificationManager.notify(notificationId, mBuilder.build())
                    }
                } else {
                    firstLoad = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Error in query", databaseError.toException())
            }
        })
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "channel"
            val description = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            // IMPORTANCE MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                importance
            )
            channel.description = description
            // Register the channel with the system; you can`t change the importance or other notification behaviors after this

            // Register the channel with the system.
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}