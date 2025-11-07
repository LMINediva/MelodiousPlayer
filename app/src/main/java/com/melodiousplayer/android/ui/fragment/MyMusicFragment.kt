package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import com.melodiousplayer.android.adapter.MyMusicAdapter
import com.melodiousplayer.android.base.BaseGridListFragment
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.MyMusicBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.MyMusicPresenterImpl
import com.melodiousplayer.android.ui.activity.AudioPlayerActivity
import com.melodiousplayer.android.util.URLProviderUtils
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
            intent.putExtra("isMyMusic", true)
            intent.putExtra("music", musicBeans[position])
            activity?.startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 解绑presenter
        presenter.destroyView()
    }

}