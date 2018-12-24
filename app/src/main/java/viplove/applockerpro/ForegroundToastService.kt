package viplove.applockerpro

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import com.rvalerio.fgchecker.AppChecker


class ForegroundToastService : Service() {
    private val NOTIFICATION_ID = 1234
    private val STOP_SERVICE = ForegroundToastService::class.java.getPackage()!!.toString() + ".stop"

    private var stopServiceReceiver: BroadcastReceiver? = null
    private var appChecker: AppChecker? = null
    private var session: Session? = null

    fun start(context: Context) {
        context.startService(Intent(context, ForegroundToastService::class.java))
    }

    fun stop(context: Context) {
        context.stopService(Intent(context, ForegroundToastService::class.java))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val res = super.onStartCommand(intent, flags, startId)
        start(baseContext)
        return Service.START_STICKY
    }

    override fun onTaskRemoved(intent: Intent) {
        super.onTaskRemoved(intent)
        val intent = Intent(this, this.javaClass)
        startService(intent)
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
        startChecker()
        createStickyNotification()
        NotificationHelper(this)
        session = Session(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopChecker()
        removeNotification()
        unregisterReceivers()
        stopSelf()
    }

    private fun startChecker() {
        appChecker = AppChecker()
        appChecker!!
            .`when`(
                packageName
            ) {
                //Toast.makeText(baseContext, "Our app is in the foreground.", Toast.LENGTH_SHORT).show()
            }
            .whenOther { packageName ->
                //Toast.makeText(baseContext, "Foreground: $packageName", Toast.LENGTH_SHORT).show()
                if (packageName == "com.whatsapp") {
                    if (session?.getStringValue(Session.CURRENT) == packageName) {
                    } else {
                        session?.getCurrent(packageName)
                        val i = Intent(this, LockActivity::class.java)
                        i.putExtra("package", packageName)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                    }
                }
                session?.getCurrent(packageName)
            }
            .timeout(500)
            .start(this)
    }

    private fun stopChecker() {
        appChecker!!.stop()
    }

    private fun registerReceivers() {
        stopServiceReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                stopSelf()
                removeNotification()
            }
        }
        registerReceiver(stopServiceReceiver, IntentFilter(STOP_SERVICE))
    }

    private fun unregisterReceivers() {
        unregisterReceiver(stopServiceReceiver)
    }

    private fun createStickyNotification(): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, NotificationHelper.Statified.ALP_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Tap to Stop Service")
                .setWhen(0)
                .build()
            /*.setContentIntent(
                PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(STOP_SERVICE),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )*/
        } else {
            NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Tap to Stop Service")
                .setContentIntent(
                    PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(STOP_SERVICE),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setWhen(0)
                .build()
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT
        manager.notify(NOTIFICATION_ID, notification)
        return notification
    }

    private fun removeNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(NOTIFICATION_ID)
    }
}