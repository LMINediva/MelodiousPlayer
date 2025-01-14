package com.melodiousplayer.android.ui.fragment

import com.melodiousplayer.android.adapter.MVListAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MVPagerBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.MVListPresenterImpl
import com.melodiousplayer.android.view.MVListView
import com.melodiousplayer.android.widget.MVItemView

/**
 * MV界面每一个页面的fragment
 */
class MVPagerFragment : BaseListFragment<MVPagerBean, VideosBean, MVItemView>(), MVListView {

    var code: String? = null

    override fun init() {
        code = arguments?.getString("args")
    }

    override fun getSpecialAdapter(): BaseListAdapter<VideosBean, MVItemView> {
        return MVListAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        return MVListPresenterImpl(code!!, this)
    }

    override fun getList(response: MVPagerBean?): List<VideosBean>? {
        return response?.videos
    }

}