package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import com.melodiousplayer.android.adapter.HomeAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.presenter.impl.HomePresenterImpl
import com.melodiousplayer.android.ui.activity.AudioPlayerActivity
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.widget.HomeItemView

/**
 * 首页在线音乐界面
 */
class HomeFragment : BaseListFragment<List<HomeItemBean>, HomeItemBean, HomeItemView>() {

    override fun onDataChanged() {
        super.onDataChanged()
    }

    override fun getSpecialAdapter(): BaseListAdapter<HomeItemBean, HomeItemView> {
        return HomeAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        return HomePresenterImpl(this)
    }

    override fun getList(response: List<HomeItemBean>?): List<HomeItemBean>? {
        return response
    }

    override fun initListener() {
        super.initListener()
        // 设置条目点击事件监听函数
        adapter.setMyListener {
            // 获取已经加载加载的音乐列表
            val homeItemBeans: List<HomeItemBean> = adapter.list
            val list: ArrayList<AudioBean> = ArrayList()
            homeItemBeans.let {
                it.forEach { homeItem ->
                    list.add(
                        AudioBean(
                            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                    + URLProviderUtils.musicPath + homeItem.url,
                            homeItem.musicSize.toLong(),
                            homeItem.title,
                            homeItem.artistName,
                            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                    + URLProviderUtils.lyricPath + homeItem.lyric,
                            true
                        )
                    )
                }
            }
            // 位置position
            val position = adapter.position
            // 跳转到音乐播放界面
            val intent = Intent(activity, AudioPlayerActivity::class.java)
            intent.putExtra("list", list)
            intent.putExtra("position", position)
            activity?.startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解绑presenter
        presenter.destroyView()
    }

}