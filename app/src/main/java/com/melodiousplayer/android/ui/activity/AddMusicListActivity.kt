package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.PagingAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.GetMVListContract
import com.melodiousplayer.android.model.MVListResultBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.GetMVListPresenterImpl
import com.melodiousplayer.android.util.ToolBarManager
import kotlin.math.ceil

/**
 * 添加音乐清单界面
 */
class AddMusicListActivity : BaseActivity(), ToolBarManager, GetMVListContract.View,
    View.OnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PagingAdapter
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var pageInfo: TextView
    private lateinit var totalInfo: TextView
    private lateinit var spinnerPageSize: Spinner
    private lateinit var token: String
    private var currentPage = 1
    private var totalPages = 1
    private var pageSize = 5
    private var total = 0L
    private var dataList = mutableListOf<VideosBean>()
    private var selectedItems = mutableSetOf<VideosBean>()
    private var getMVListPresenter = GetMVListPresenterImpl(this)

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
        totalInfo = findViewById(R.id.totalInfo)
        spinnerPageSize = findViewById(R.id.spinnerPageSize)
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        // 设置RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PagingAdapter(dataList, selectedItems) { item, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
        }
        recyclerView.adapter = adapter
        // 分页获取MV数据
        getMVList(currentPage, pageSize)
        updatePageInfo()
        // 设置分页大小选择器
        ArrayAdapter.createFromResource(
            this, R.array.page_sizes, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPageSize.adapter = adapter
        }
    }

    override fun initListener() {
        // 页面大小下拉菜单点击事件
        spinnerPageSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pageSize = resources.getStringArray(R.array.page_sizes)[position].toInt()
                currentPage = 1
                getMVList(currentPage, pageSize)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        // 按钮点击事件
        btnPrevious.setOnClickListener(this)
        btnNext.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPrevious -> {
                if (currentPage > 1) {
                    currentPage--
                    getMVList(currentPage, pageSize)
                }
            }

            R.id.btnNext -> {
                if (currentPage < totalPages) {
                    currentPage++
                    getMVList(currentPage, pageSize)
                }
            }
        }
    }

    /**
     * 分页获取MV列表数据
     */
    private fun getMVList(currentPage: Int, pageSize: Int) {
        val pageBean = PageBean("", currentPage, pageSize)
        getMVListPresenter.getMVList(token, pageBean)
    }

    /**
     * 更新MV列表数据
     */
    private fun updateMVList() {
        totalPages = ceil(total.toDouble() / pageSize).toInt()
        adapter.updateItems(dataList)
        updatePageInfo()
        updateButtonStates()
    }

    /**
     * 更新页面信息
     */
    private fun updatePageInfo() {
        pageInfo.text = "${currentPage}/${totalPages}"
        totalInfo.text = "共${total}条"
    }

    /**
     * 更新上一页和下一页按钮的状态
     */
    private fun updateButtonStates() {
        btnPrevious.isEnabled = currentPage > 1
        btnNext.isEnabled = currentPage < totalPages
    }

    override fun onGetMVListSuccess(result: MVListResultBean) {
        dataList = result.mvList!!
        total = result.total!!
        updateMVList()
    }

    override fun onGetMVListFailed() {
        myToast(getString(R.string.get_mv_list_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}