package com.melodiousplayer.android.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.melodiousplayer.android.R
import com.melodiousplayer.android.ui.activity.AboutActivity
import com.melodiousplayer.android.ui.activity.ClearCacheActivity

/**
 * 设置界面布局
 */
class SettingFragment : PreferenceFragment() {

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addPreferencesFromResource(R.xml.setting)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPreferenceTreeClick(
        preferenceScreen: PreferenceScreen?,
        preference: Preference?
    ): Boolean {
        when (preference?.key) {
            "clear_cache" -> {
                // 跳转到清除缓存界面
                activity.startActivity(Intent(activity, ClearCacheActivity::class.java))
            }

            "about" -> {
                // 跳转到关于界面
                activity.startActivity(Intent(activity, AboutActivity::class.java))
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

}