package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.widget.MVListItemView

/**
 * 悦单界面适配器
 */
class MVListAdapter : RecyclerView.Adapter<MVListAdapter.MVListHolder>() {

    class MVListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MVListHolder {
        return MVListHolder(MVListItemView(parent?.context))
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: MVListHolder, position: Int) {
    }

}