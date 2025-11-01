package com.melodiousplayer.android.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.manager.DownloadManager
import com.melodiousplayer.android.R
import com.melodiousplayer.android.contract.CheckUpdateContract
import com.melodiousplayer.android.model.VersionUpdateResultBean
import com.melodiousplayer.android.presenter.impl.CheckUpdatePresenterImpl
import com.melodiousplayer.android.ui.activity.AboutActivity
import com.melodiousplayer.android.ui.activity.ClearCacheActivity
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 设置界面布局
 */
class SettingFragment : PreferenceFragmentCompat(), CheckUpdateContract.View,
    OnButtonClickListener {

    private var manager: DownloadManager? = null
    private var apkFileName: String? = null
    private val checkUpdatePresenter = CheckUpdatePresenterImpl(this)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
    }

    override fun onPreferenceTreeClick(preference: androidx.preference.Preference): Boolean {
        when (preference.key) {
            "check_update" -> {
                // 检查更新
                val packageManager = activity?.packageManager
                val packageInfo =
                    activity?.packageName?.let { packageManager?.getPackageInfo(it, 0) }
                val versionName = packageInfo?.versionName
                if (versionName != null) {
                    checkUpdatePresenter.checkUpdate(versionName)
                }
            }

            "clear_cache" -> {
                // 跳转到清除缓存界面
                activity?.startActivity(Intent(activity, ClearCacheActivity::class.java))
            }

            "about" -> {
                // 跳转到关于界面
                activity?.startActivity(Intent(activity, AboutActivity::class.java))
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    /**
     * 实现可在子线程中安全的弹出消息
     */
    private fun myToast(msg: String) {
        // 通过父Activity来调用runOnUiThread
        activity?.runOnUiThread {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCheckUpdateSuccess(result: VersionUpdateResultBean) {
        if (result.versionUpdate?.update.equals("Yes")) {
            apkFileName = result.versionUpdate?.apk_file_url
            manager = activity?.let {
                DownloadManager.Builder(it).run {
                    result.versionUpdate?.apk_file_url?.let {
                        apkUrl(URLProviderUtils.getAPKUpdateUrl() + it)
                    }
                    result.versionUpdate?.apk_file_url?.let { apkName(it) }
                    smallIcon(R.mipmap.ic_launcher)
                    showNewerToast(true)
                    apkVersionCode(2)
                    apkVersionName("v" + result.versionUpdate?.new_version)
                    result.versionUpdate?.target_size?.let { apkSize(it + "MB") }
                    result.versionUpdate?.update_log?.let { apkDescription(it) }
                    enableLog(true)
                    jumpInstallPage(true)
                    dialogButtonTextColor(Color.WHITE)
                    showNotification(true)
                    showBgdToast(false)
                    result.versionUpdate?.constraint?.let { forcedUpgrade(it) }
                    dialogImage(R.drawable.top_8)
                    onButtonClickListener(this@SettingFragment)
                    build()
                }
            }
            manager?.download()
        } else {
            myToast("已经是最新版本")
        }
    }

    override fun onCheckUpdateFailed(msg: String?) {
        myToast("检查更新失败")
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onButtonClick(id: Int) {
        if (id == 0 && apkFileName !== null) {
            val editor = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)!!.edit()
            // 将APK文件名存储到SharedPreferences文件中
            editor.putString("apk_file_name", apkFileName)
            editor.apply()
        }
    }

}