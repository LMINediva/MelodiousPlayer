package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import android.os.Bundle
import com.melodiousplayer.android.adapter.MusicAdapter
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.base.BaseListFragment
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.presenter.impl.MusicPresenterImpl
import com.melodiousplayer.android.ui.activity.AudioPlayerActivity
import com.melodiousplayer.android.ui.activity.MainActivity
import com.melodiousplayer.android.util.NotificationUtil
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.widget.MusicItemView

/**
 * 首页在线音乐界面
 */
class MusicFragment : BaseListFragment<List<MusicBean>, MusicBean, MusicItemView>() {

    private var notificationON: Boolean = false
    private var notificationKeyExist: Boolean = false

    companion object {
        /**
         * 单例，返回此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MusicFragment {
            return MusicFragment()
        }
    }

    override fun getSpecialAdapter(): BaseListAdapter<MusicBean, MusicItemView> {
        return MusicAdapter()
    }

    override fun getSpecialPresenter(): BaseListPresenter {
        return MusicPresenterImpl(this)
    }

    override fun getList(response: List<MusicBean>?): List<MusicBean>? {
        return response
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            notificationKeyExist = NotificationUtil.isNotificationKeyExist(it)
        }
    }

    override fun initData() {
        super.initData()
        context?.let {
            // 检查通知是否打开
            notificationON = NotificationUtil.isNotificationEnabled(it)
            notificationKeyExist = NotificationUtil.isNotificationKeyExist(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (context as MainActivity).onMusicFragmentAdded()
    }

    override fun initListener() {
        super.initListener()
        // 设置条目点击事件监听函数
        adapter.setMyListener {
            context?.let {
                if (!notificationKeyExist) {
                    if (!notificationON) {
                        NotificationUtil.setNotification(it, false)
                        NotificationUtil.showOpenNotificationDialog(it)
                    } else {
                        NotificationUtil.setNotification(it, true)
                        // 获取已经加载加载的音乐列表
                        val musicBeans: List<MusicBean> = adapter.list
                        val list: ArrayList<AudioBean> = ArrayList()
                        musicBeans.let {
                            it.forEach { music ->
                                var onlineLyric: String?
                                if (music.lyric.isNullOrBlank()) {
                                    onlineLyric = null
                                } else {
                                    onlineLyric =
                                        URLProviderUtils.protocol + URLProviderUtils.serverAddress + URLProviderUtils.lyricPath + music.lyric
                                }
                                list.add(
                                    AudioBean(
                                        URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                                + URLProviderUtils.musicPath + music.url,
                                        music.musicSize!!.toLong(),
                                        music.title!!,
                                        music.artistName,
                                        onlineLyric,
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
                } else {
                    if (!notificationON) {
                        NotificationUtil.setNotification(it, false)
                    } else {
                        NotificationUtil.setNotification(it, true)
                    }
                    // 获取已经加载加载的音乐列表
                    val musicBeans: List<MusicBean> = adapter.list
                    val list: ArrayList<AudioBean> = ArrayList()
                    musicBeans.let {
                        it.forEach { music ->
                            var onlineLyric: String?
                            if (music.lyric.isNullOrBlank()) {
                                onlineLyric = null
                            } else {
                                onlineLyric =
                                    URLProviderUtils.protocol + URLProviderUtils.serverAddress + URLProviderUtils.lyricPath + music.lyric
                            }
                            list.add(
                                AudioBean(
                                    URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                            + URLProviderUtils.musicPath + music.url,
                                    music.musicSize!!.toLong(),
                                    music.title!!,
                                    music.artistName,
                                    onlineLyric,
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
    }

}