package viplove.applockerpro

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build

class NotificationHelper(base: Context?) : ContextWrapper(base) {

    object Statified {
        val ALP_CHANNEL_ID = "viplove.applockerpro.ALP"
        val ALP_CHANNEL_NAME = "Default Channel"

    }

    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannels()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val alpChannel =
            NotificationChannel(
                Statified.ALP_CHANNEL_ID,
                Statified.ALP_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MIN
            )
        alpChannel.enableLights(false)
        alpChannel.enableVibration(false)
        alpChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        getManager().createNotificationChannel(alpChannel)

    }

    public fun getManager(): NotificationManager {
        if (manager == null)
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return manager!!
    }
}