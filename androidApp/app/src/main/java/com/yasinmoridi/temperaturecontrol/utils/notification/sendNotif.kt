package com.yasinmoridi.temperaturecontrol.utils.notification

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_DATA_DESC
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_DATA_TITLE
import com.yasinmoridi.temperaturecontrol.utils.PermissionManager
import java.util.concurrent.TimeUnit

fun sendNotifWithPermission(
    context: Context,
    permissionManager: PermissionManager,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    title: String,
    desc: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (!permissionManager.isGranted(Manifest.permission.POST_NOTIFICATIONS)) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            sendLocalNotif(context, title, desc)
        }
    } else {
        sendLocalNotif(context, title, desc)
    }
}

fun sendLocalNotif(
    context: Context,
    title: String,
    desc: String
) {
    val data = workDataOf(
        NOTIF_DATA_TITLE to title,
        NOTIF_DATA_DESC to desc
    )

    val request = OneTimeWorkRequestBuilder<NotifWorker>()
        .setInputData(data)
        .setInitialDelay(5, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}

