package com.melodiousplayer.android.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.ui.fragment.MVPagerFragment

class MVPagerAdapter(val context: Context, val list: List<MVAreaBean>?, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        // 如果不为null，返回list.size；为空返回0
        return list?.size ?: 0
    }

    override fun getItem(position: Int): Fragment {
        // 第一种数据传递方式
        // val fragment = MVPagerFragment()
        val bundle = Bundle()
        bundle.putString("args", list?.get(position)?.code)
        // fragment.arguments = bundle
        // 第二种fragment传递数据的方式
        val fragment = Fragment.instantiate(context, MVPagerFragment::class.java.name, bundle)
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list?.get(position)?.name
    }

}