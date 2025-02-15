package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.LyricBean
import com.melodiousplayer.android.util.LyricLoader
import com.melodiousplayer.android.util.LyricUtil

/**
 * 自定义歌词view
 */
class LyricView : View {

    // 通过惰性加载创建画笔Paint
    val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    val list by lazy { ArrayList<LyricBean>() }
    var centerLine = 10
    var viewW: Int = 0
    var viewH: Int = 0
    var bigSize = 0f
    var smallSize = 0f
    var white = 0
    var green = 0
    var lineHeight = 0
    var duration = 0
    var progress = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        bigSize = resources.getDimension(R.dimen.bigSize)
        smallSize = resources.getDimension(R.dimen.smallSize)
        white = resources.getColor(R.color.white)
        green = resources.getColor(R.color.green)
        lineHeight = resources.getDimensionPixelOffset(R.dimen.lineHeight)
        // 画笔在x轴方向确定位置是通过中间位置确定坐标
        paint.textAlign = Paint.Align.CENTER
        // 循环添加歌词bean
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // drawSingleLine(canvas)
        drawMultiLine(canvas)
    }

    /**
     * 绘制多行居中文本
     */
    private fun drawMultiLine(canvas: Canvas) {
        // 求居中行偏移y
        // 行可用时间
        var lineTime = 0
        // 判断是否是最后一行居中
        if (centerLine == list.size - 1) {
            // 最后一行居中，行可用时间 = duration - 最后一行开始时间
            lineTime = duration - list.get(centerLine).startTime
        } else {
            // 其他行居中，行可用时间 = 下一行开始时间 - 居中行开始时间
            val centerS = list.get(centerLine).startTime
            val nextS = list.get(centerLine + 1).startTime
            lineTime = nextS - centerS
        }
        // 偏移时间 = progress - 居中行开始时间
        val offsetTime = progress - list.get(centerLine).startTime
        // 偏移百分比 = 偏移时间 / 行可用时间
        val offsetPercent = offsetTime / (lineTime.toFloat())
        // 偏移y = 偏移百分比 * 行高
        val offsetY = offsetPercent * lineHeight
        val centerText = list.get(centerLine).content
        val bounds = Rect()
        paint.getTextBounds(centerText, 0, centerText.length, bounds)
        val textH = bounds.height()
        // 居中行y值
        val centerY = viewH / 2 + textH / 2 - offsetY
        for ((index, value) in list.withIndex()) {
            if (index == centerLine) {
                // 绘制居中行
                paint.color = green
                paint.textSize = bigSize
            } else {
                // 其他行
                paint.color = white
                paint.textSize = smallSize
            }
            val curX = viewW / 2
            val curY = centerY + (index - centerLine) * lineHeight
            // 超出边界处理
            // 处理超出上边界歌词
            if (curY < 0) continue
            // 处理超出下边界歌词
            if (curY > viewH + lineHeight) break
            val curText = list.get(index).content
            canvas?.drawText(curText, curX.toFloat(), curY.toFloat(), paint)
        }
    }

    /**
     * 绘制单行居中文本
     */
    private fun drawSingleLine(canvas: Canvas) {
        // 初始化画笔Paint的颜色和大小
        paint.textSize = bigSize
        paint.color = green
        val text = "正在加载歌词..."
        // 求文本的宽度和高度
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val textW = bounds.width()
        val textH = bounds.height()
        // x = viewW / 2 - textW / 2
        val x = viewW / 2
        // y = viewH / 2 + textH / 2
        val y = viewH / 2 + textH / 2
        // 绘制内容
        canvas?.drawText(text, x.toFloat(), y.toFloat(), paint)
    }

    /**
     * 布局之后执行
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewW = w
        viewH = h
    }

    /**
     * 传递当前播放进度，实现歌词播放
     */
    fun updateProgress(progress: Int) {
        this.progress = progress
        // 获取居中行行号
        // 先判断居中行是否是最后一行
        if (progress >= list.get(list.size - 1).startTime) {
            // progress >= 最后一行开始时间
            // 最后一行居中
            centerLine = list.size - 1
        } else {
            // 其他行居中，循环遍历集合
            for (index in 0 until list.size - 1) {
                // progress >= 当前行开始时间 & progress < 下一行开始时间
                val curStartTime = list.get(index).startTime
                val nextStartTime = list.get(index + 1).startTime
                if (progress >= curStartTime && progress < nextStartTime) {
                    centerLine = index
                    break
                }
            }
        }
        // 找到居中行之后绘制当前多行歌词
        invalidate() // onDraw方法
        // postInvalidate() // onDraw方法，可以在子线程中刷新
        // requestLayout() // view布局参数改变时刷新
    }

    /**
     * 设置当前播放歌曲总时长
     */
    fun setSongDuration(duration: Int) {
        this.duration = duration
    }

    /**
     * 设置歌曲播放名称
     * 解析歌词文件并且添加到集合中
     */
    fun setSongName(name: String) {
        this.list.clear()
        this.list.addAll(LyricUtil.parseLyric(LyricLoader.loadLyricFile(name)))
    }

}