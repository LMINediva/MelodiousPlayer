package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.widget.HomeItemView

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeHolder>() {

    private var list = ArrayList<HomeItemBean>()

    class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    /**
     * 更新数据
     */
    fun updateList(list: List<HomeItemBean>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        return HomeHolder(HomeItemView(parent.context))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        // 条目数据
        val data = list.get(position)
        // 条目view
        val itemView = holder.itemView as HomeItemView
        // 条目刷新
        itemView.setData(data)
    }

}