package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.melodiousplayer.android.R

/**
 * 上拉加载更多
 */
class LoadMoreView : RelativeLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.view_loadmore, this)
    }

    fun setData() {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val lastPageText: TextView = findViewById(R.id.lastPageText)
        progressBar.visibility = View.GONE
        lastPageText.visibility = View.VISIBLE
    }

}