package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
class SettingFragment : PreferenceFragment(), CheckUpdateContract.View,
    OnButtonClickListener {

    private var manager: DownloadManager? = null
    private val checkUpdatePresenter = CheckUpdatePresenterImpl(this)

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addPreferencesFromResource(R.xml.setting)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPreferenceTreeClick(
        preferenceScreen: PreferenceScreen?,
        preference: Preference?
    ): Boolean {
        when (preference?.key) {
            "check_update" -> {
                // 检查更新
                val packageManager = activity.packageManager
                val packageInfo = packageManager.getPackageInfo(activity.packageName, 0)
                val versionName = packageInfo.versionName
                if (versionName != null) {
                    checkUpdatePresenter.checkUpdate(versionName)
                }
            }

            "clear_cache" -> {
                // 跳转到清除缓存界面
                activity.startActivity(Intent(activity, ClearCacheActivity::class.java))
            }

            "about" -> {
                // 跳转到关于界面
                activity.startActivity(Intent(activity, AboutActivity::class.java))
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
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
            manager = DownloadManager.Builder(activity).run {
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

    }

}