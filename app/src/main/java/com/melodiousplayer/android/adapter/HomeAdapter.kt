package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.widget.HomeItemView

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeHolder>() {

    class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        return HomeHolder(HomeItemView(parent.context))
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
    }

}