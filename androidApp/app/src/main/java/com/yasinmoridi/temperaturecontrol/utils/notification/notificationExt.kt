package com.yasinmoridi.temperaturecontrol.utils.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yasinmoridi.temperaturecontrol.R
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_CHANNEL_DESC
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_CHANNEL_ID
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_CHANNEL_NAME


@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun Context.showNotification(
    title: String,
    desc: String
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val notification = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(title)
        .setContentText(desc)
        .setAutoCancel(true)
        .build()

    val manager = NotificationManagerCompat.from(this)
    manager.notify(System.currentTimeMillis().toInt(), notification)

}

fun Context.createNotificationChannel() {
    val channel = NotificationChannel(
        NOTIF_CHANNEL_ID,
        NOTIF_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply { description = NOTIF_CHANNEL_DESC }
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}
