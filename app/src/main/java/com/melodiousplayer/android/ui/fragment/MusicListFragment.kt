package com.melodiousplayer.android.ui.fragment

import com.melodiousplayer.android.adapter.MusicListAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.presenter.impl.MusicListPresenterImpl
import com.melodiousplayer.android.widget.MusicListItemView

/**
 * 悦单界面
 */
class MusicListFragment :
    BaseListFragment<MusicListBean, PlayListsBean, MusicListItemView>() {

    override fun getSpecialAdapter(): BaseListAdapter<PlayListsBean, MusicListItemView> {
        return MusicListAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        return MusicListPresenterImpl(this)
    }

    override fun getList(response: MusicListBean?): List<PlayListsBean>? {
        return response?.playLists
    }

}