package com.melodiousplayer.android.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.ui.fragment.MyMVFragment
import com.melodiousplayer.android.ui.fragment.MyMusicFragment
import com.melodiousplayer.android.ui.fragment.MyMusicListFragment


class MyWorkPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    currentUser: UserBean,
    token: String
) : FragmentPagerAdapter(fm) {

    private val bundle = Bundle()

    init {
        bundle.putSerializable("user", currentUser)
        bundle.putString("token", token)
    }

    // 顶部TabLayout的标题
    private val TAB_TITLES = arrayOf(
        R.string.my_work_tab_text_1,
        R.string.my_work_tab_text_2,
        R.string.my_work_tab_text_3
    )

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                val myMusicFragment = MyMusicFragment.newInstance()
                myMusicFragment.arguments = bundle
                return myMusicFragment
            }

            1 -> {
                val myMVFragment = MyMVFragment.newInstance()
                myMVFragment.arguments = bundle
                return myMVFragment
            }

            2 -> {
                val myMusicListFragment = MyMusicListFragment.newInstance()
                myMusicListFragment.arguments = bundle
                return myMusicListFragment
            }
        }
        val myMusicFragment = MyMusicFragment.newInstance()
        myMusicFragment.arguments = bundle
        return myMusicFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // 顶部显示多少个页，不要超过TAB_TITLES的总数
        return TAB_TITLES.size
    }

}