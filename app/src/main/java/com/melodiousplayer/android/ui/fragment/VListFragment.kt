package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.melodiousplayer.android.base.BaseFragment

/**
 * V榜
 */
class VListFragment : BaseFragment() {

    override fun initView(): View? {
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.RED)
        tv.text = javaClass.simpleName
        return tv
    }

}