package com.melodiousplayer.android.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import com.melodiousplayer.android.R

object NotificationUtil {

    /**
     * 检查通知显示是否开启
     */
    fun isNotificationEnabled(context: Context): Boolean {
        val isOpened: Boolean = try {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return isOpened
    }

    /**
     * 显示开启通知提示弹窗
     */
    fun showOpenNotificationDialog(context: Context) {
        context.let {
            val builder = AlertDialog.Builder(it)
            val inflater = LayoutInflater.from(it)
            val dialogView = inflater.inflate(R.layout.dialog_open_notification, null)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            alertDialog.show()
            val cancelButton = dialogView.findViewById<Button>(R.id.cancel)
            val openButton = dialogView.findViewById<Button>(R.id.open)
            cancelButton.setOnClickListener {
                setNotification(context, false)
                alertDialog.dismiss()
            }
            openButton.setOnClickListener {
                setNotification(context, true)
                alertDialog.dismiss()
                // 引导用户打开通知权限
                gotoSetNotification(context)
            }
        }
    }

    /**
     * 引导用户打开通知
     */
    fun gotoSetNotification(context: Context) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName())
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
            intent.putExtra("app_package", context.getPackageName())
            intent.putExtra("app_uid", context.getApplicationInfo().uid)
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
            intent.setData(Uri.fromParts("package", context.getPackageName(), null))
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 检查SharedPreferences是否存在通知设置属性
     */
    fun isNotificationKeyExist(context: Context): Boolean {
        val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val exists = prefs.contains("enable_notification")
        if (exists) {
            return true
        } else {
            return false
        }
    }

    /**
     * 设置SharedPreferences中的通知设置属性
     */
    fun setNotification(context: Context, isEnable: Boolean) {
        val editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editor.putBoolean("enable_notification", isEnable)
        editor.apply()
    }

    /**
     * 获取SharedPreferences中的通知设置属性
     */
    fun getNotification(context: Context): Boolean {
        val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val isEnableNotification = prefs.getBoolean("enable_notification", false)
        return isEnableNotification
    }

}