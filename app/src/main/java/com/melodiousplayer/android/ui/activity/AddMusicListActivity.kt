package com.melodiousplayer.android.ui.activity

import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.PagingAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 添加音乐清单界面
 */
class AddMusicListActivity : BaseActivity(), ToolBarManager {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PagingAdapter
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var pageInfo: TextView
    private lateinit var total: TextView
    private lateinit var spinnerPageSize: Spinner
    private var currentPage = 1
    private var totalPages = 1
    private var pageSize = 0
    private val dataList = mutableListOf<VideosBean>()
    private val selectedItems = mutableSetOf<VideosBean>()

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_music_list
    }

    override fun initData() {
        initAddMVToolBar()
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
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        pageInfo = findViewById(R.id.pageInfo)
        total = findViewById(R.id.total)
        spinnerPageSize = findViewById(R.id.spinnerPageSize)
        // 设置RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PagingAdapter(dataList, selectedItems) {item, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
        }
        recyclerView.adapter = adapter
    }

    /**
     * 初始化MV列表
     */
    private fun initMVList() {
        
    }

}