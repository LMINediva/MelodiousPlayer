package com.melodiousplayer.android.util

import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.ui.fragment.HomeFragment
import com.melodiousplayer.android.ui.fragment.MVFragment
import com.melodiousplayer.android.ui.fragment.MVListFragment
import com.melodiousplayer.android.ui.fragment.VListFragment

/**
 * 管理fragment的util类
 */
class FragmentUtil private constructor() { // 私有化构造函数

    // 首页
    val homeFragment by lazy { HomeFragment() }

    // MV
    val mvFragment by lazy { MVFragment() }

    // V榜
    val vListFragment by lazy { VListFragment() }

    // 悦单
    val mvListFragment by lazy { MVListFragment() }

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
            R.id.tab_vList -> return vListFragment
            R.id.tab_mvList -> return mvListFragment
        }
        return null
    }

}