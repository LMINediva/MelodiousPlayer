package com.melodiousplayer.android.ui.activity

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        immersionFull(window)
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

    /**
     * 沉浸模式（全屏模式）
     * 设置全屏的方法
     * 参数window在activity或AppCompatActivity都带有
     */
    fun immersionFull(window: Window) {
        hideSystemBars(window)
        useSpecialScreen(window)
    }

    /**
     * 隐藏状态栏，显示内容上移到状态栏
     */
    private fun hideSystemBars(window: Window) {
        //占满全屏，activity绘制将状态栏也加入绘制范围。
        //如此即使使用BEHAVIOR_SHOW_BARS_BY_SWIPE或BEHAVIOR_SHOW_BARS_BY_TOUCH
        //也不会因为状态栏的显示而导致activity的绘制区域出现变形
        //使用刘海屏也需要这一句进行全屏处理
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //隐藏状态栏和导航栏 以及交互
        WindowInsetsControllerCompat(window, window.decorView).let {
            //隐藏状态栏和导航栏
            //用于WindowInsetsCompat.Type.systemBars()隐藏两个系统栏
            //用于WindowInsetsCompat.Type.statusBars()仅隐藏状态栏
            //用于WindowInsetsCompat.Type.navigationBars()仅隐藏导航栏
            it.hide(WindowInsetsCompat.Type.systemBars())
            //交互效果
            //BEHAVIOR_SHOW_BARS_BY_SWIPE 下拉状态栏操作可能会导致activity画面变形
            //BEHAVIOR_SHOW_BARS_BY_TOUCH 未测试到与BEHAVIOR_SHOW_BARS_BY_SWIPE的明显差异
            //BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE 下拉或上拉的屏幕交互操作会显示状态栏和导航栏
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * 扩展使用刘海屏
     */
    private fun useSpecialScreen(window: Window) {
        //允许window 的内容可以上移到刘海屏状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
    }

}