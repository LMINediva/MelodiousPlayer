package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import cn.jzvd.JzvdStd
import com.melodiousplayer.android.R

class MyJzvdStdView : JzvdStd {

    private lateinit var rotateLeft: Button
    private lateinit var rotateRight: Button

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId(): Int {
        return R.layout.activity_jiaozi_player
    }

    override fun init(context: Context?) {
        super.init(context)
        // 视频画面向左旋转90度按钮
        rotateLeft = findViewById(R.id.rotate_left)
        // 视频画面向右旋转90度按钮
        rotateRight = findViewById(R.id.rotate_right)
        // 设置旋转视频画面按钮初始为不可见
        rotateLeft.visibility = GONE
        rotateRight.visibility = GONE
        // 设置旋转视频画面按钮的点击事件
        rotateLeft.setOnClickListener(this)
        rotateRight.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
    }

    override fun onClickUiToggle() {
        super.onClickUiToggle()
        if (state == STATE_PREPARING) {
            rotateLeft.visibility = GONE
            rotateRight.visibility = GONE
        } else if (state == STATE_PLAYING) {
            rotateLeft.visibility = GONE
            rotateRight.visibility = GONE
        } else if (state == STATE_PAUSE) {
            rotateLeft.visibility = VISIBLE
            rotateRight.visibility = VISIBLE
        }
    }

}