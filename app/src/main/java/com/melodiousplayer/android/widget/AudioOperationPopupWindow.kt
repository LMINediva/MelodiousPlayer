package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.util.ThemeUtil

class AudioOperationPopupWindow(
    context: Context,
    listener: OnClickListener,
    val window: Window
) :
    PopupWindow() {

    // 记录当前应用程序窗体透明度
    private var alpha: Float = 0f
    private var currentContext: Context
    private var popupOperationBackground: LinearLayout
    private var cancel: ImageView
    private var edit: ImageView
    private var editText: TextView
    private var delete: ImageView
    private var deleteText: TextView

    init {
        // 记录当前窗体的透明度
        alpha = window.attributes.alpha
        // 设置布局
        val view = LayoutInflater.from(context).inflate(R.layout.popup_operation, null, false)
        popupOperationBackground = view.findViewById(R.id.popup_operation_background)
        cancel = view.findViewById(R.id.cancel)
        edit = view.findViewById(R.id.edit)
        editText = view.findViewById(R.id.editText)
        delete = view.findViewById(R.id.delete)
        deleteText = view.findViewById(R.id.deleteText)
        // 设置点击事件
        cancel.setOnClickListener(listener)
        edit.setOnClickListener(listener)
        delete.setOnClickListener(listener)
        contentView = view
        // 设置宽度和高度
        width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置高度为屏幕高度1/5
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        manager.defaultDisplay.getSize(point)
        val windowHeight = point.y
        height = (windowHeight * 1) / 5
        // 设置获取焦点
        isFocusable = true
        // 设置外部点击
        isOutsideTouchable = true
        // 能够响应返回按钮（低版本PopupWindow点击返回按钮能够dismiss的关键）
        setBackgroundDrawable(ColorDrawable())
        // 处理PopupWindow弹出和隐藏的动画
        animationStyle = R.style.bottom_popup
        currentContext = context
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        // PopupWindow已经显示
        val attributes = window.attributes
        val isDarkTheme = currentContext.let { ThemeUtil.isDarkTheme(it) }
        if (isDarkTheme) {
            val imageColor = currentContext.getColor(R.color.imageNight)
            val textColor = currentContext.getColor(R.color.imageTextNight)
            popupOperationBackground.setBackgroundResource(R.drawable.round_corner_night)
            edit.setColorFilter(imageColor, PorterDuff.Mode.SRC_IN)
            edit.setBackgroundResource(R.drawable.circle_shape_night)
            delete.setBackgroundResource(R.drawable.circle_shape_night)
            delete.setColorFilter(imageColor, PorterDuff.Mode.SRC_IN)
            cancel.setColorFilter(imageColor, PorterDuff.Mode.SRC_IN)
            editText.setTextColor(textColor)
            deleteText.setTextColor(textColor)
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