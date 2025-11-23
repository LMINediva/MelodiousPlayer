package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import com.melodiousplayer.android.adapter.MusicAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.presenter.impl.MusicPresenterImpl
import com.melodiousplayer.android.ui.activity.AudioPlayerActivity
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.widget.MusicItemView

/**
 * 首页在线音乐界面
 */
class MusicFragment : BaseListFragment<List<MusicBean>, MusicBean, MusicItemView>() {

    override fun getSpecialAdapter(): BaseListAdapter<MusicBean, MusicItemView> {
        return MusicAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        return MusicPresenterImpl(this)
    }

    override fun getList(response: List<MusicBean>?): List<MusicBean>? {
        return response
    }

    override fun initListener() {
        super.initListener()
        // 设置条目点击事件监听函数
        adapter.setMyListener {
            // 获取已经加载加载的音乐列表
            val musicBeans: List<MusicBean> = adapter.list
            val list: ArrayList<AudioBean> = ArrayList()
            musicBeans.let {
                it.forEach { music ->
                    list.add(
                        AudioBean(
                            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                    + URLProviderUtils.musicPath + music.url,
                            music.musicSize!!.toLong(),
                            music.title!!,
                            music.artistName,
                            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                    + URLProviderUtils.lyricPath + music.lyric,
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

}