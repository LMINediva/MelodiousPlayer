package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.PopupWindow
import com.melodiousplayer.android.R

class PlayListPopupWindow(context: Context, adapter: BaseAdapter, listener: OnItemClickListener) :
    PopupWindow() {

    init {
        // 设置布局
        val view = LayoutInflater.from(context).inflate(R.layout.popup_playlist, null, false)
        // 获取ListView
        val listView = view.findViewById<ListView>(R.id.listView)
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
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        // PopupWindow已经显示
    }

    override fun dismiss() {
        super.dismiss()
        // PopupWindow已经隐藏
    }

}