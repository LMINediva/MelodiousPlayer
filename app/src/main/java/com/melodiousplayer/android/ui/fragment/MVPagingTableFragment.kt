package com.melodiousplayer.android.ui.fragment

import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment

class MVPagingTableFragment : BaseFragment() {

    private lateinit var view: View

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_mv_pagination_controls, null)
        }
        return view
    }

}