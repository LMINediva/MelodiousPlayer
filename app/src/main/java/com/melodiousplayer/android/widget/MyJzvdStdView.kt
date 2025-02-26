package com.melodiousplayer.android.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import cn.jzvd.JzvdStd
import com.melodiousplayer.android.R

class MyJzvdStdView : JzvdStd {

    private lateinit var rotateLeft: Button
    private lateinit var rotateRight: Button
    private lateinit var hideButtonHandler: Handler

    // 延时隐藏按钮的Runnable对象
    private val hideButtonRunnable = Runnable {
        rotateLeft.visibility = GONE
        rotateRight.visibility = GONE
    }

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
        // 设置旋转视频画面按钮的点击事件
        rotateLeft.setOnClickListener(this)
        rotateRight.setOnClickListener(this)
        // 创建Handler对象用于延迟执行Runnable对象
        hideButtonHandler = Handler()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
    }

    /**
     * 开始播放视频
     */
    override fun startVideo() {
        super.startVideo()
        // 隐藏视频画面向左和向右转90度按钮
        rotateLeft.visibility = GONE
        rotateRight.visibility = GONE
    }

    /**
     * 暂停播放视频
     */
    override fun onStatePause() {
        super.onStatePause()
        // 取消2.2秒后视频画面向左和向右转90度按钮
        hideButtonHandler.removeCallbacks(hideButtonRunnable)
    }

    /**
     * 点击视频播放界面空白处函数
     */
    override fun onClickUiToggle() {
        super.onClickUiToggle()
        // 点击视频播放界面空白处，显示视频画面向左和向右转90度按钮
        rotateLeft.visibility = VISIBLE
        rotateRight.visibility = VISIBLE
        if (state == STATE_PLAYING) {
            // 播放状态，2.2秒后视频画面向左和向右转90度按钮
            hideButtonHandler.postDelayed(hideButtonRunnable, 2200)
        } else if (state == STATE_PAUSE) {
            println("暂停状态")
            // 暂停状态，切换视频画面向左和向右转90度按钮隐藏和显示状态
            println("rotateLeft: " + rotateLeft.visibility)
            println("rotateRight: " + rotateRight.visibility)
            if (rotateLeft.visibility == VISIBLE && rotateRight.visibility == VISIBLE) {
                rotateLeft.visibility = GONE
                rotateRight.visibility = GONE
            } else {
                rotateLeft.visibility = VISIBLE
                rotateRight.visibility = VISIBLE
            }
        }
    }

}