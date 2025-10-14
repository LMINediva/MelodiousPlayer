package com.melodiousplayer.android.ui.fragment

import com.melodiousplayer.android.adapter.MyMusicAdapter
import com.melodiousplayer.android.base.BaseGridListFragment
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.MyMusicBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.MyMusicPresenterImpl
import com.melodiousplayer.android.widget.MyMusicItemView

/**
 * 我的音乐界面
 */
class MyMusicFragment : BaseGridListFragment<MyMusicBean, MusicBean, MyMusicItemView>() {

    private lateinit var currentUser: UserBean
    private lateinit var token: String

    companion object {
        /**
         * 单例，返回此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MyMusicFragment {
            return MyMusicFragment()
        }
    }

    override fun init() {
        currentUser = arguments?.getSerializable("user") as UserBean
        token = arguments?.getString("token").toString()
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun getSpecialAdapter(): BaseListAdapter<MusicBean, MyMusicItemView> {
        return MyMusicAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        val pageBean = PageBean("", 0, 20, currentUser)
        return MyMusicPresenterImpl(this, token, pageBean)
    }

    override fun getList(response: MyMusicBean?): List<MusicBean>? {
        return response?.musicList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解绑presenter
        presenter.destroyView()
    }

}