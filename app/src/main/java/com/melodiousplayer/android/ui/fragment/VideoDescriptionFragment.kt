package com.melodiousplayer.android.ui.fragment

import android.view.View
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment

/**
 * 视频描述界面
 */
class VideoDescriptionFragment : BaseFragment() {

    private lateinit var view: View
    private lateinit var descriptionTextView: TextView
    private var description: String? = null

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_video_description, null)
        }
        return view
    }

    override fun initData() {
        descriptionTextView = view.findViewById(R.id.description)
        // 获取视频描述数据
        description = arguments?.getString("description")
        if (description != null) {
            descriptionTextView.text = addFirstLineIndent(description!!, 4)
        } else {
            descriptionTextView.text = addFirstLineIndent("暂无视频描述", 4)
        }
    }

    private fun addFirstLineIndent(text: String, indent: Int): String {
        val indentSpaces = String(CharArray(indent)).replace('\u0000', '\t')
        return indentSpaces + text
    }

}