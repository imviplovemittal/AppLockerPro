package viplove.applockerpro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CommonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, ForegroundToastService::class.java))
    }

}