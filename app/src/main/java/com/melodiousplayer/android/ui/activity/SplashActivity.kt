package com.melodiousplayer.android.ui.activity

import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity

/**
 * 欢迎界面
 */
class SplashActivity : BaseActivity(), ViewPropertyAnimatorListener {

    private lateinit var imageView: ImageView

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initData() {
        imageView = findViewById(R.id.imageView)
        // 设置欢迎界面缩放动画
        ViewCompat.animate(imageView)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setListener(this)
            .setDuration(2000)
    }

    override fun onAnimationStart(view: View) {

    }

    override fun onAnimationEnd(view: View) {
        // 进入主界面
        startActivityAndFinish<MainActivity>()
    }

    override fun onAnimationCancel(view: View) {

    }

}