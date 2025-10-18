package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import com.melodiousplayer.android.adapter.MyMusicListAdapter
import com.melodiousplayer.android.base.BaseGridListFragment
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MyMusicListBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.MyMusicListPresenterImpl
import com.melodiousplayer.android.ui.activity.MusicListInformationActivity
import com.melodiousplayer.android.widget.MyMusicListItemView
import java.io.Serializable

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

    override fun onDestroyView() {
        super.onDestroyView()
        // 解绑presenter
        presenter.destroyView()
    }

}