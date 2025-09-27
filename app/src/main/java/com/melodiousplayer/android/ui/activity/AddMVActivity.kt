package com.melodiousplayer.android.ui.activity

import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.MVAreasContract
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.presenter.impl.MVAreasPresenterImpl
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 添加MV界面
 */
class AddMVActivity : BaseActivity(), ToolBarManager, AdapterView.OnItemSelectedListener,
    MVAreasContract.View {

    private lateinit var title: EditText
    private lateinit var artistName: EditText
    private lateinit var description: EditText
    private lateinit var areaSpinner: Spinner
    private lateinit var posterPicture: ImageView
    private lateinit var posterPictureError: TextView
    private lateinit var thumbnailPicture: ImageView
    private lateinit var thumbnailPictureError: TextView
    private lateinit var mv: ImageView
    private lateinit var mvName: TextView
    private lateinit var mvError: TextView
    private lateinit var addMV: Button
    private lateinit var adapter: ArrayAdapter<String>
    private val items = mutableListOf<String>()
    private val mvAreasPresenterImpl = MVAreasPresenterImpl(this)

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_mv
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
        title = findViewById(R.id.title)
        artistName = findViewById(R.id.artistName)
        description = findViewById(R.id.description)
        areaSpinner = findViewById(R.id.areaSpinner)
        posterPicture = findViewById(R.id.posterPicture)
        posterPictureError = findViewById(R.id.posterPictureError)
        thumbnailPicture = findViewById(R.id.thumbnailPicture)
        thumbnailPictureError = findViewById(R.id.thumbnailPictureError)
        mv = findViewById(R.id.mv)
        mvName = findViewById(R.id.mvName)
        mvError = findViewById(R.id.mvError)
        addMV = findViewById(R.id.addMV)
        mvAreasPresenterImpl.getMVAreas()
        // 创建一个ArrayAdapter使用simple_spinner_item布局文件作为下拉列表的样式
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        // 设置下拉列表的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 将ArrayAdapter设置给Spinner
        areaSpinner.adapter = adapter
    }

    override fun initListener() {
        // 设置Spinner选中项变更监听器
        areaSpinner.onItemSelectedListener = this
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

    /**
     * Spinner选中项变更事件
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println("选中的项：${parent?.getItemAtPosition(position)}")
    }

    /**
     * Spinner没有选中项事件
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onGetMVAreasSuccess(result: List<MVAreaBean>) {
        adapter.clear()
        for (mvAreaBean in result) {
            items.add(mvAreaBean.name)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onGetMVAreasFailed() {
        myToast(getString(R.string.get_mv_areas_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }


}