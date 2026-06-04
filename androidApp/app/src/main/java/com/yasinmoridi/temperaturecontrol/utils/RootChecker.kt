package com.yasinmoridi.temperaturecontrol.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object RootChecker {

    // su files checker
    private fun checkForSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su.backup",
            "/system/xbin/mu"
        )
        return paths.any { File(it).exists() }
    }

    // check for trend root apps
    @SuppressLint("PrivateApi")
    private fun checkRootApps(): Boolean {
        val rootAppsPackages = arrayOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine"
        )
        return rootAppsPackages.any {
            try {
                Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null)?.let { context ->
                        val pm = (context as Context).packageManager
                        pm.getPackageInfo(it, 0)
                        true
                    } == true
            } catch (_: Exception) {
                false
            }
        }
    }

    // run code 'which su'
    private fun canExecuteSu(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            output != null
        } catch (e: Exception) {
            false
        }
    }

    // build tags checker
    private fun hasTestKeys(): Boolean {
        return Build.TAGS?.contains("test-keys") == true
    }

    // main fun for root checker
    fun isDeviceRooted(): Boolean {
        return checkForSuBinary() ||
                checkRootApps() ||
                canExecuteSu() ||
                hasTestKeys()
    }

}
