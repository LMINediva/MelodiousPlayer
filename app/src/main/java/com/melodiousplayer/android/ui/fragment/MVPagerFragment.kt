package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import com.melodiousplayer.android.adapter.MVListAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MVPagerBean
import com.melodiousplayer.android.model.VideoPlayBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.MVListPresenterImpl
import com.melodiousplayer.android.ui.activity.JiaoZiVideoPlayerActivity
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.view.MVListView
import com.melodiousplayer.android.widget.MVItemView

/**
 * MV界面中的每一个子页面的fragment
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

    override fun initListener() {
        super.initListener()
        // 设置条目点击事件监听函数
        adapter.setMyListener {
            val videoPlayBean =
                VideoPlayBean(
                    it.id, it.title, it.url, it.thumbnailPic, it.description
                )
            // 跳转到视频播放界面
            val intent = Intent(activity, JiaoZiVideoPlayerActivity::class.java)
            intent.putExtra("item", videoPlayBean)
            activity?.startActivity(intent)
        }
    }

}