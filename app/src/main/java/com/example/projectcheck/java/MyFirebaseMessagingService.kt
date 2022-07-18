package com.example.projectcheck.java

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.projectcheck.R
import com.example.projectcheck.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService:FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {

        super.onMessageReceived(message)

        Log.d(TAG,"From: ${message.from}")

        message.data.isNotEmpty().let{
            Log.d(TAG,"Message data Payload: ${message.from}")
        }

        message.notification?.let{
            Log.d(TAG,"Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {

        super.onNewToken(token)
        Log.e(TAG,"Refreshed Token: ${token}")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token:String?){
        //
    }

    private fun sendNotification(messageBody:String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notifications)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Title")
            .setContentText("Message")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"Channel ProjectCheck title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())
    }

    companion object{
        private const val TAG = "MyFirebaseMSGService"
    }
}