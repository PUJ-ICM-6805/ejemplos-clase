package com.icm2330.ServicesNotifications.services

import android.content.Context
import androidx.work.CoroutineWorker
import android.util.Log
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class BasicWorkManagerService(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    val TAG = BasicWorkManagerService::class.java.name

    override suspend fun doWork(): Result {
        // Get the number of milliseconds to wait from the input data
        val milliSeconds = inputData.getInt("milliAseconds", 10000)

        // Wait for the specified number of milliseconds
        delay(milliSeconds.toLong())

        // Log that the service has finished waiting
        Log.i(TAG, "Service Finished Waiting")

        // Loop for 1000 seconds, logging every second
        for (i in 0 until 100) {
            delay(1000)
            Log.i(TAG, "Service finished Waiting $i")
        }

        // Return Result.success() to indicate that the work was successful
        return Result.success()
    }
}