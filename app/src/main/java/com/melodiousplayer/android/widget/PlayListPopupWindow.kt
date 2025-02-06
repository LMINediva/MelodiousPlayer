package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ListView
import android.widget.PopupWindow
import com.melodiousplayer.android.R

class PlayListPopupWindow(context: Context) : PopupWindow() {

    init {
        // 设置布局
        val view = LayoutInflater.from(context).inflate(R.layout.popup_playlist, null, false)
        // 获取ListView
        val listView = view.findViewById<ListView>(R.id.listView)
        // 适配
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
    }

}