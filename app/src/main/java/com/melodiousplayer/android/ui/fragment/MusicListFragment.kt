package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import android.os.Bundle
import com.melodiousplayer.android.adapter.MusicListAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.presenter.impl.MusicListPresenterImpl
import com.melodiousplayer.android.ui.activity.MainActivity
import com.melodiousplayer.android.ui.activity.MusicListInformationActivity
import com.melodiousplayer.android.widget.MusicListItemView
import java.io.Serializable

/**
 * 悦单界面
 */
class MusicListFragment :
    BaseListFragment<MusicListBean, PlayListsBean, MusicListItemView>() {

    companion object {
        /**
         * 单例，返回此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MusicListFragment {
            return MusicListFragment()
        }
    }

    override fun getSpecialAdapter(): BaseListAdapter<PlayListsBean, MusicListItemView> {
        return MusicListAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        return MusicListPresenterImpl(this)
    }

    override fun getList(response: MusicListBean?): List<PlayListsBean>? {
        return response?.playLists
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (context as MainActivity).onMusicListFragmentAdded()
    }

    override fun initListener() {
        super.initListener()
        // 设置条目点击事件监听函数
        adapter.setMyListener {
            // 跳转到悦单中的MV列表显示界面
            val intent = Intent(activity, MusicListInformationActivity::class.java)
            intent.putExtra("title", it.title)
            intent.putExtra("mvList", it.mvList as Serializable)
            activity?.startActivity(intent)
        }
    }

}