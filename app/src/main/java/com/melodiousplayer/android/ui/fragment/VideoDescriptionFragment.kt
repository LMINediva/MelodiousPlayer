package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.melodiousplayer.android.base.BaseFragment

/**
 * 视频描述界面
 */
class VideoDescriptionFragment : BaseFragment() {

    private var description: String? = null

    override fun initView(): View? {
        // 获取视频描述数据
        description = arguments?.getString("description")
        val tv = TextView(context)
        tv.setTextColor(Color.BLACK)
        if (description != null) {
            tv.text = "\u3000\u3000" + description
        } else {
            tv.text = "\u3000\u3000" + "暂无视频描述"
        }
        return tv
    }

}