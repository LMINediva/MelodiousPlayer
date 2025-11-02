package com.melodiousplayer.android.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.manager.DownloadManager
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.contract.CheckUpdateContract
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.VersionUpdateResultBean
import com.melodiousplayer.android.presenter.impl.CheckUpdatePresenterImpl
import com.melodiousplayer.android.ui.activity.AboutActivity
import com.melodiousplayer.android.ui.activity.ClearCacheActivity
import com.melodiousplayer.android.ui.activity.FeedBackActivity
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 设置界面布局
 */
class SettingFragment : BaseFragment(), CheckUpdateContract.View,
    OnButtonClickListener, View.OnClickListener {

    private lateinit var view: View
    private lateinit var checkUpdate: LinearLayout
    private lateinit var clearCache: LinearLayout
    private lateinit var feedback: LinearLayout
    private lateinit var about: LinearLayout
    private lateinit var currentUser: UserBean
    private var manager: DownloadManager? = null
    private var apkFileName: String? = null
    private val checkUpdatePresenter = CheckUpdatePresenterImpl(this)

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.setting_preference, null)
        }
        return view
    }

    override fun initData() {
        checkUpdate = view.findViewById(R.id.check_update)
        clearCache = view.findViewById(R.id.clear_cache)
        feedback = view.findViewById(R.id.feedback)
        about = view.findViewById(R.id.about)
        val userSerialized = arguments?.getSerializable("user")
        val isLogin = arguments?.getBoolean("isLogin")!!
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
        if (isLogin) {
            feedback.visibility = View.VISIBLE
        } else {
            feedback.visibility = View.GONE
        }
    }

    override fun initListener() {
        checkUpdate.setOnClickListener(this)
        clearCache.setOnClickListener(this)
        feedback.setOnClickListener(this)
        about.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.check_update -> {
                // 检查更新
                val packageManager = activity?.packageManager
                val packageInfo =
                    activity?.packageName?.let { packageManager?.getPackageInfo(it, 0) }
                val versionName = packageInfo?.versionName
                if (versionName != null) {
                    checkUpdatePresenter.checkUpdate(versionName)
                }
            }

            R.id.clear_cache -> {
                // 跳转到清除缓存界面
                activity?.startActivity(Intent(activity, ClearCacheActivity::class.java))
            }

            R.id.feedback -> {
                // 进入用户反馈界面，传递用户信息
                val intent = Intent(activity, FeedBackActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }

            R.id.about -> {
                // 跳转到关于界面
                activity?.startActivity(Intent(activity, AboutActivity::class.java))
            }
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