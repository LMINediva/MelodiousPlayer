package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MusicListInformationAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.DeleteMusicListContract
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.DeleteMusicListPresenterImpl
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 悦单中的MV列表界面
 */
class MusicListInformationActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    DeleteMusicListContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cancel: ImageView
    private lateinit var edit: ImageView
    private lateinit var delete: ImageView
    private lateinit var mvList: MutableSet<VideosBean>
    private lateinit var currentMusicList: PlayListsBean
    private var isMyMusicList: Boolean = false
    private var token: String? = null
    private var popupWindow: PopupWindow? = null
    private val deleteMusicListPresenter = DeleteMusicListPresenterImpl(this)

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_music_list_information
    }

    override fun initData() {
        // 设置Toolbar标题
        toolbarTitle.text = intent.getStringExtra("title")
        mvList = intent.getSerializableExtra("mvList") as MutableSet<VideosBean>
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        isMyMusicList = intent.getBooleanExtra("isMyMusicList", false)
        if (isMyMusicList) {
            token = intent.getStringExtra("token")
            val musicListSerialized = intent.getSerializableExtra("musicList")
            if (musicListSerialized != null) {
                currentMusicList = musicListSerialized as PlayListsBean
            }
        }
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = MusicListInformationAdapter(mvList.toList())
        recyclerView.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel -> popupWindow?.dismiss()
            R.id.edit -> editMyMusicList()
            R.id.delete -> {
                popupWindow?.dismiss()
                AlertDialog.Builder(this).apply {
                    setTitle("提示")
                    setMessage("确定要删除悦单吗？")
                    setCancelable(false)
                    setPositiveButton("确定") { dialog, which ->
                        token?.let {
                            deleteMusicListPresenter.deleteMusicList(
                                it,
                                arrayOf(currentMusicList.id!!)
                            )
                        }
                    }
                    setNegativeButton("取消") { dialog, which ->
                    }
                    show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 设置action按钮
        menuInflater.inflate(R.menu.more_operation, menu)
        if (!isMyMusicList) {
            menu?.findItem(R.id.more)?.setVisible(false)
        }
        return true
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

            R.id.more -> {
                showPopupWindow()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 从底部显示PopupWindow
     */
    private fun showPopupWindow() {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (popupWindow == null) {
            val view = layoutInflater.inflate(R.layout.popup_operation, null)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = 350
            cancel = view.findViewById(R.id.cancel)
            edit = view.findViewById(R.id.edit)
            delete = view.findViewById(R.id.delete)
            popupWindow = PopupWindow(view, width, height, true)
            cancel.setOnClickListener(this)
            edit.setOnClickListener(this)
            delete.setOnClickListener(this)
        }
        popupWindow?.animationStyle = R.style.bottom_popup
        popupWindow?.isOutsideTouchable = true
        popupWindow?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        // 显示PopupWindow，参数为锚点View和重力、偏移量，这里设置为底部弹出
        popupWindow?.showAtLocation(window.decorView, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 修改我的音乐清单
     */
    private fun editMyMusicList() {
        popupWindow?.dismiss()
        // 进入添加音乐清单界面，传递音乐清单信息
        val intent = Intent(this, AddMusicListActivity::class.java)
        intent.putExtra("isMyMusicList", true)
        intent.putExtra("musicList", currentMusicList)
        startActivity(intent)
    }

    override fun onDeleteMusicListSuccess(result: ResultBean) {
        myToast(getString(R.string.delete_music_list_success))
        // 返回我的作品界面，传递用户信息，并退出MusicListInformationActivity
        val intent = Intent(this, MyWorkActivity::class.java)
        intent.putExtra("user", currentMusicList.sysUser)
        intent.putExtra("refresh", true)
        startActivityForResult(intent, 1)
        finish()
    }

    override fun onDeleteMusicListFailed(result: ResultBean) {
        myToast(getString(R.string.delete_music_list_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}