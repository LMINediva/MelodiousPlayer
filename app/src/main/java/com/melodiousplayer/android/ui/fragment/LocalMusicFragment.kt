package com.melodiousplayer.android.ui.fragment

import android.Manifest
import android.content.AsyncQueryHandler
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.LocalMusicAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.ui.activity.AudioPlayerActivity

/**
 * 本地音乐
 */
class LocalMusicFragment : BaseFragment() {

    var adapter: LocalMusicAdapter? = null
    private lateinit var view: View
    private lateinit var listView: ListView

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_local_music, null)
        }
        return view
    }

    override fun initData() {
        listView = view.findViewById(R.id.listView)
        adapter = LocalMusicAdapter(context, null)
        // 动态权限申请
        handlePermission()
        // 检查通知是否打开
        var notificationON = false
        context?.let {
            notificationON = isNotificationEnabled(it)
            if (!notificationON) {
                // 引导用户打开通知权限
                gotoSetNotification(it)
            }
        }
    }

    /**
     * 处理权限问题
     */
    private fun handlePermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
        )
        // 查看是否有权限
        val hasAllPermissions = context?.let {
            hasPermissions(it, permissions)
        }
        if (hasAllPermissions == true) {
            // 已经获取权限
            loadSongs()
        } else {
            // 没有获取权限
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                ) {
                    // 需要弹出
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("温馨提示")
                        setMessage("我们只会访问音乐文件，不会访问隐私照片")
                        setCancelable(false)
                        setPositiveButton("OK") { dialog, which ->
                            myRequestPermission()
                        }
                        setNegativeButton("Cancel") { dialog, which ->

                        }
                        show()
                    }
                } else {
                    // 不需要弹出
                    myRequestPermission()
                }
            }
        }
    }

    /**
     * 检查多个权限是否授权
     */
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 真正申请权限
     */
    private fun myRequestPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
        )
        requestPermissions(permissions, 1)
    }

    /**
     * 接收权限授权结果
     * requestCode 请求码
     * permissions 权限申请数组
     * grantResults 申请之后的结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                var allPermissionsGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    }
                }
                if (allPermissionsGranted) {
                    loadSongs()
                }
            }
        }
    }

    private fun loadSongs() {
        // 加载音乐列表数据
        val resolver = context?.contentResolver
        val handler = object : AsyncQueryHandler(resolver) {
            override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
                // 查询完成回调，主线程中
                // 设置数据源，刷新adapter
                (cookie as LocalMusicAdapter).swapCursor(cursor)
            }
        }
        // 开始查询
        handler.startQuery(
            0, adapter,
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media._ID,
                Media.DATA,
                Media.SIZE,
                Media.DISPLAY_NAME,
                Media.ARTIST
            ),
            null, null, null
        )
    }

    /**
     * 检查通知显示是否开启
     */
    fun isNotificationEnabled(context: Context): Boolean {
        var isOpened = false
        isOpened = try {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return isOpened
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

    override fun initListener() {
        listView.adapter = adapter
        // 设置条目点击事件
        listView.setOnItemClickListener { parent, view, position, id ->
            // 获取数据集合
            val cursor = adapter?.getItem(position) as Cursor
            // 通过当前位置cursor获取整个播放列表
            val list: ArrayList<AudioBean> = AudioBean.getAudioBeans(cursor)
            // 位置position
            val intent = Intent(activity, AudioPlayerActivity::class.java)
            intent.putExtra("list", list)
            intent.putExtra("position", position)
            // 跳转到音乐播放界面
            activity?.startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 界面销毁，关闭cursor
        // 获取adapter中的cursor，然后进行关闭，将adapter中的cursor设置为null
        adapter?.changeCursor(null)
    }

}