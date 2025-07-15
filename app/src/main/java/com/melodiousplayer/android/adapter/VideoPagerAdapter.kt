package com.melodiousplayer.android.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.melodiousplayer.android.model.VideoPlayBean
import com.melodiousplayer.android.ui.fragment.DefaultFragment
import com.melodiousplayer.android.ui.fragment.VideoDescriptionFragment

class VideoPagerAdapter(fm: FragmentManager, private val videoPlayBean: VideoPlayBean?) :
    FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                val videoDescriptionFragment = VideoDescriptionFragment()
                videoPlayBean?.let {
                    val args = Bundle()
                    args.putString("description", videoPlayBean.description)
                    videoDescriptionFragment.arguments = args
                }
                return videoDescriptionFragment
            }
        }
        return DefaultFragment()
    }

}