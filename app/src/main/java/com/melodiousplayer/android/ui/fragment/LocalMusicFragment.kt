package com.melodiousplayer.android.ui.fragment

import android.Manifest
import android.content.AsyncQueryHandler
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.LocalMusicAdapter
import com.melodiousplayer.android.base.BaseFragment

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
    }

    /**
     * 处理权限问题
     */
    private fun handlePermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        // 查看是否有权限
        val checkSelfPermission = context?.let {
            ActivityCompat.checkSelfPermission(it, permission)
        }
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            // 已经获取权限
            loadSongs()
        } else {
            // 没有获取权限
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

    /**
     * 真正申请权限
     */
    private fun myRequestPermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(requireActivity(), permissions, 1)
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    override fun initListener() {
        listView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        // 界面销毁，关闭cursor
        // 获取adapter中的cursor，然后进行关闭，将adapter中的cursor设置为null
        adapter?.changeCursor(null)
    }

}