package com.melodiousplayer.android.ui.fragment

import com.melodiousplayer.android.adapter.MyMusicListAdapter
import com.melodiousplayer.android.base.BaseGridListFragment
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MyMusicListBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.MyMusicListPresenterImpl
import com.melodiousplayer.android.widget.MyMusicListItemView

/**
 * 我的悦单界面
 */
class MyMusicListFragment :
    BaseGridListFragment<MyMusicListBean, PlayListsBean, MyMusicListItemView>() {

    private lateinit var currentUser: UserBean
    private lateinit var token: String

    companion object {
        /**
         * 单例，返回给定编号的此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MyMusicListFragment {
            return MyMusicListFragment()
        }
    }

    override fun init() {
        currentUser = arguments?.getSerializable("user") as UserBean
        token = arguments?.getString("token").toString()
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun getSpecialAdapter(): BaseListAdapter<PlayListsBean, MyMusicListItemView> {
        return MyMusicListAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        val pageBean = PageBean("", 0, 20, currentUser)
        return MyMusicListPresenterImpl(this, token, pageBean)
    }

    override fun getList(response: MyMusicListBean?): List<PlayListsBean>? {
        return response?.playList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解绑presenter
        presenter.destroyView()
    }

}