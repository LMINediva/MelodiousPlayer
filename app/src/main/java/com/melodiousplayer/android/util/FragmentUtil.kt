package com.melodiousplayer.android.util

import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.ui.fragment.HomeFragment
import com.melodiousplayer.android.ui.fragment.MVFragment
import com.melodiousplayer.android.ui.fragment.MusicListFragment
import com.melodiousplayer.android.ui.fragment.LocalMusicFragment

/**
 * 管理fragment的util类
 */
class FragmentUtil private constructor() { // 私有化构造函数

    // 首页
    val homeFragment by lazy { HomeFragment() }

    // MV
    val mvFragment by lazy { MVFragment() }

    // 本地音乐
    val localMusicFragment by lazy { LocalMusicFragment() }

    // 悦单
    val musicListFragment by lazy { MusicListFragment() }

    companion object {
        val fragmentUtil by lazy { FragmentUtil() }
    }

    /**
     * 根据tabId获取对应的fragment
     */
    fun getFragment(tabId: Int): BaseFragment? {
        when (tabId) {
            R.id.tab_home -> return homeFragment
            R.id.tab_mv -> return mvFragment
            R.id.tab_local_music_list -> return localMusicFragment
            R.id.tab_music_list -> return musicListFragment
        }
        return null
    }

}