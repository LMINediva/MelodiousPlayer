package com.melodiousplayer.android.ui.activity

import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 设置界面
 */
class SettingActivity : BaseActivity(), ToolBarManager {

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun initData() {
        initSettingToolbar()
    }

}