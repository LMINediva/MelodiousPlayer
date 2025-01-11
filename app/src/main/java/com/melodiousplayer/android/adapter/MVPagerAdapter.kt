package com.melodiousplayer.android.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.ui.fragment.MVPagerFragment

class MVPagerAdapter(val list: List<MVAreaBean>?, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        // 如果不为null，返回list.size；为空返回0
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Fragment {
        return MVPagerFragment()
    }

}