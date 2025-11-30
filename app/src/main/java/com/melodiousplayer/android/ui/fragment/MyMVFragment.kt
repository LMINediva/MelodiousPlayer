package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import com.melodiousplayer.android.adapter.MyMVAdapter
import com.melodiousplayer.android.base.BaseGridListFragment
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MyMVBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.VideoPlayBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.MyMVPresenterImpl
import com.melodiousplayer.android.ui.activity.JiaoZiVideoPlayerActivity
import com.melodiousplayer.android.widget.MyMVItemView

/**
 * 我的MV界面
 */
class MyMVFragment : BaseGridListFragment<MyMVBean, VideosBean, MyMVItemView>() {

    private lateinit var currentUser: UserBean
    private lateinit var token: String

    companion object {
        /**
         * 单例，返回给定编号的此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MyMVFragment {
            return MyMVFragment()
        }
    }

    override fun init() {
        currentUser = arguments?.getSerializable("user") as UserBean
        token = arguments?.getString("token").toString()
    }

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun getSpecialAdapter(): BaseListAdapter<VideosBean, MyMVItemView> {
        return MyMVAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        val pageBean = PageBean("", 0, 20, currentUser)
        return MyMVPresenterImpl(this, token, pageBean)
    }

    override fun getList(response: MyMVBean?): List<VideosBean>? {
        return response?.mvList
    }

    override fun initListener() {
        super.initListener()
        // 设置条目点击事件监听函数
        adapter.setMyListener {
            val videoPlayBean =
                VideoPlayBean(
                    it.id!!, it.title!!, it.url!!, it.thumbnailPic!!, it.description!!
                )
            // 跳转到视频播放界面
            val intent = Intent(activity, JiaoZiVideoPlayerActivity::class.java)
            intent.putExtra("item", videoPlayBean)
            intent.putExtra("isMyMV", true)
            intent.putExtra("token", token)
            intent.putExtra("mv", it)
            activity?.startActivityForResult(intent, 2)
        }
    }

}