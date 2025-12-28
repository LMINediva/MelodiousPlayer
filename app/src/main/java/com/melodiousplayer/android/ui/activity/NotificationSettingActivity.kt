package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.NotificationUtil
import com.melodiousplayer.android.util.ToolBarManager

class NotificationSettingActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var notificationSwitch: SwitchCompat
    private var isNotificationON: Boolean = false

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_notification_setting
    }

    override fun initData() {
        initNotificationSettingToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        notificationSwitch = findViewById(R.id.notification_switch)
        // 检查通知是否打开
        if (!NotificationUtil.isNotificationKeyExist(this)) {
            isNotificationON = NotificationUtil.isNotificationEnabled(this)
        } else {
            isNotificationON = NotificationUtil.getNotification(this)
        }
        if (isNotificationON) {
            notificationSwitch.isChecked = true
        } else {
            notificationSwitch.isChecked = false
        }
    }

    override fun initListener() {
        notificationSwitch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.notification_switch -> {
                if (notificationSwitch.isChecked) {
                    showOpenNotificationDialog(this)
                } else {
                    showCloseNotificationDialog(this)
                }
            }
        }
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        // 检查通知是否打开
        isNotificationON = NotificationUtil.isNotificationEnabled(this)
        if (isNotificationON) {
            notificationSwitch.isChecked = true
        } else {
            notificationSwitch.isChecked = false
        }
    }

    /**
     * 显示开启通知提示弹窗
     */
    private fun showOpenNotificationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_open_notification, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel)
        val openButton = dialogView.findViewById<Button>(R.id.open)
        cancelButton.setOnClickListener {
            notificationSwitch.isChecked = isNotificationON
            alertDialog.dismiss()
        }
        openButton.setOnClickListener {
            alertDialog.dismiss()
            // 引导用户打开通知权限
            NotificationUtil.gotoSetNotification(context)
        }
    }

    /**
     * 显示开启通知提示弹窗
     */
    private fun showCloseNotificationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_open_notification, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()
        val notificationTitle = dialogView.findViewById<TextView>(R.id.notification_title)
        val notificationContent = dialogView.findViewById<TextView>(R.id.notification_content)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel)
        val openButton = dialogView.findViewById<Button>(R.id.open)
        val color = resources.getColor(R.color.qq_red, context.theme)
        notificationTitle.text = getString(R.string.close_notification_title)
        notificationTitle.setTextColor(color)
        notificationContent.text = getString(R.string.close_notification)
        openButton.setBackgroundResource(R.drawable.button_close_background)
        openButton.text = "去关闭"
        cancelButton.setOnClickListener {
            notificationSwitch.isChecked = isNotificationON
            alertDialog.dismiss()
        }
        openButton.setOnClickListener {
            alertDialog.dismiss()
            // 引导用户打开通知权限
            NotificationUtil.gotoSetNotification(context)
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}