package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.melodiousplayer.android.base.BaseFragment

/**
 * 视频播放界面
 */
class DefaultFragment : BaseFragment() {

    override fun initView(): View? {
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.RED)
        tv.text = "暂无"
        return tv
    }

}