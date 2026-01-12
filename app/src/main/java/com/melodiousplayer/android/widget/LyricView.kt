package com.melodiousplayer.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.LyricBean
import com.melodiousplayer.android.util.LyricLoader
import com.melodiousplayer.android.util.LyricUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 自定义歌词view
 */
class LyricView : View {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    // 通过惰性加载创建画笔Paint
    private val paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val list by lazy { ArrayList<LyricBean>() }
    private var centerLine = 0
    private var viewW: Int = 0
    private var viewH: Int = 0
    private var bigSize = 0f
    private var smallSize = 0f
    private var white = 0
    private var green = 0
    private var lineHeight = 0
    private var duration = 0
    private var progress = 0

    // 指定是否可以通过progress进度更新歌词，默认为true
    private var updateByProgress = true
    private var downY = 0f
    private var offsetY = 0f
    private var markY = 0f

    // 进度回调函数
    private var listener: ((progress: Int) -> Unit)? = null

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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (list.size == 0) {
            // 歌词没有加载
            drawSingleLine(canvas)
        } else {
            // 歌词已经加载
            drawMultiLine(canvas)
        }
    }

    /**
     * 绘制多行居中文本
     */
    private fun drawMultiLine(canvas: Canvas) {
        if (updateByProgress) {
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
            offsetY = offsetPercent * lineHeight
        }
        val centerText = list.get(centerLine).content
        val bounds = Rect()
        paint.getTextBounds(centerText, 0, centerText.length, bounds)
        val textH = bounds.height()
        // 居中行y值
        val centerY = viewH / 2 + textH / 2 - offsetY
        list.withIndex().forEach { (index, value) ->
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
            if (curY < 0) return@forEach
            // 处理超出下边界歌词
            if (curY > viewH + lineHeight) return@forEach
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
        if (!updateByProgress) return
        if (list.size == 0) return
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
        val file = LyricLoader.loadLyricFile(name)
        val isFileExists = file.exists()
        if (isFileExists) {
            // 在IO调度器上启动一个协程
            GlobalScope.launch(Dispatchers.IO) {
                // 在这里执行异步操作
                this@LyricView.list.clear()
                this@LyricView.list.addAll(LyricUtil.parseLyric(file))
            }
        } else {
            this@LyricView.list.clear()
            this@LyricView.list.addAll(LyricUtil.parseLyric(null))
        }
    }

    /**
     * 设置在线歌曲播放名称
     * 解析歌词文件并且添加到集合中
     */
    fun setOnlineSongName(url: String?) {
        if (url != null && url.endsWith(".lrc")) {
            // 在IO调度器上启动一个协程
            GlobalScope.launch(Dispatchers.IO) {
                // 在这里执行异步操作
                // 加载在线音乐
                val request = Request.Builder()
                    .url(url)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Failed to load lyric.")
                    this@LyricView.list.clear()
                    this@LyricView.list.addAll(LyricUtil.parseOnlineLyric(response.body?.string()))
                    // 关闭响应，释放资源
                    response.close()
                }
            }
        } else {
            this@LyricView.list.clear()
            this@LyricView.list.addAll(LyricUtil.parseOnlineLyric(null))
        }
    }

    /**
     * 歌词控件手势事件处理
     * 1.手指按下时，停止通过进度更新歌词
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 停止通过进度更新歌词
                    updateByProgress = false
                    // 记录手指按下的y值
                    downY = event.y
                    // 记录原来进度已经更新y
                    markY = this.offsetY
                }

                MotionEvent.ACTION_MOVE -> {
                    // 当前y值
                    val endY = event.y
                    // 求手指移动的y值
                    val offsetY = downY - endY
                    // 重新设置居中行偏移
                    this.offsetY = offsetY + markY
                    // 如果最终的y的偏移值大于行高，重新确定居中行
                    if (Math.abs(this.offsetY) >= lineHeight) {
                        // 求居中行行号偏移
                        val offsetLine = (this.offsetY / lineHeight).toInt()
                        centerLine += offsetLine
                        // 对居中行做边界处理
                        if (centerLine < 0)
                            centerLine = 0
                        else if (centerLine > list.size - 1)
                            centerLine = list.size - 1
                        // downY重新设置
                        this.downY = endY
                        // 重新确定偏移y
                        this.offsetY = this.offsetY % lineHeight
                        // 重新记录y的偏移量
                        markY = this.offsetY
                        // 更新播放进度
                        listener?.invoke(list.get(centerLine).startTime)
                    }
                    // 重新绘制
                    invalidate()
                }

                MotionEvent.ACTION_UP -> updateByProgress = true
            }
        }
        return true
    }

    /**
     * 设置进度回调函数的函数
     */
    fun setProgressListener(listener: (progress: Int) -> Unit) {
        this.listener = listener
    }

}