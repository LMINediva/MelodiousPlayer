package com.melodiousplayer.android.ui.activity

import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MusicListInformationAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 悦单中的MV列表界面
 */
class MusicListInformationActivity : BaseActivity(), ToolBarManager {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mvList: MutableSet<VideosBean>

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
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = MusicListInformationAdapter(mvList.toList())
        recyclerView.adapter = adapter
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

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}