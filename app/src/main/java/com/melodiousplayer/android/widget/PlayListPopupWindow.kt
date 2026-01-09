package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.PopupWindow
import com.melodiousplayer.android.R
import com.melodiousplayer.android.util.ThemeUtil

class PlayListPopupWindow(
    context: Context,
    adapter: BaseAdapter,
    listener: OnItemClickListener,
    val window: Window
) :
    PopupWindow() {

    // 记录当前应用程序窗体透明度
    private var alpha: Float = 0f
    private var currentContext: Context
    private var listView: ListView

    init {
        // 记录当前窗体的透明度
        alpha = window.attributes.alpha
        // 设置布局
        val view = LayoutInflater.from(context).inflate(R.layout.popup_playlist, null, false)
        // 获取ListView
        listView = view.findViewById(R.id.listView)
        // 适配
        listView.adapter = adapter
        // 设置列表条目点击事件
        listView.setOnItemClickListener(listener)
        contentView = view
        // 设置宽度和高度
        width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置高度为屏幕高度3/5
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        manager.defaultDisplay.getSize(point)
        val windowHeight = point.y
        height = (windowHeight * 3) / 5
        // 设置获取焦点
        isFocusable = true
        // 设置外部点击
        isOutsideTouchable = true
        // 能够响应返回按钮（低版本PopupWindow点击返回按钮能够dismiss的关键）
        setBackgroundDrawable(ColorDrawable())
        // 处理PopupWindow弹出和隐藏的动画
        animationStyle = R.style.popup
        currentContext = context
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        // PopupWindow已经显示
        val attributes = window.attributes
        val isDarkTheme = currentContext.let { ThemeUtil.isDarkTheme(it) }
        if (isDarkTheme) {
            listView.setBackgroundResource(R.color.darkGrayNight)
        }
        attributes.alpha = 0.3f
        // 设置到应用程序窗体上
        window.attributes = attributes
    }

    override fun dismiss() {
        super.dismiss()
        // PopupWindow已经隐藏，恢复应用程序窗体透明度
        val attributes = window.attributes
        attributes.alpha = alpha
        window.attributes = attributes
    }

}