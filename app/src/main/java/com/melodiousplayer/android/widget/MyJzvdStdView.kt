package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import cn.jzvd.JzvdStd
import com.melodiousplayer.android.R

class MyJzvdStdView: JzvdStd {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId(): Int {
        return R.layout.activity_jiaozi_player
    }

}