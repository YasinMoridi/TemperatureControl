package com.yasinmoridi.temperaturecontrol.utils.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_BASE_DESC
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_BASE_TITLE
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_DATA_DESC
import com.yasinmoridi.temperaturecontrol.utils.Constants.NOTIF_DATA_TITLE

class NotifWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val title = inputData.getString(NOTIF_DATA_TITLE) ?: NOTIF_BASE_TITLE
            val desc = inputData.getString(NOTIF_DATA_DESC) ?: NOTIF_BASE_DESC

            applicationContext.showNotification(title, desc)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
