package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * 自定义歌词view
 */
class LyricView : View {

    // 通过惰性加载创建画笔Paint
    val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val text = "正在加载歌词..."
        // x = viewW / 2 + textW / 2
        // y = viewH / 2 + textH / 2
        // 绘制内容
        canvas?.drawText(text, 0f, 0f, paint)
    }

    /**
     * 布局之后执行
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

}