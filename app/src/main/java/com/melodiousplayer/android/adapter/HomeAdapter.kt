package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.widget.HomeItemView
import com.melodiousplayer.android.widget.LoadMoreView

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeHolder>() {

    private var list = ArrayList<HomeItemBean>()

    class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    /**
     * 更新数据
     */
    fun updateList(list: List<HomeItemBean>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    /**
     * 加载更多
     */
    fun loadMoreList(list: List<HomeItemBean>?) {
        list?.let {
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        if (viewType == 1) {
            // 最后一条
            return HomeHolder(LoadMoreView(parent.context))
        } else {
            // 普通条目
            return HomeHolder(HomeItemView(parent.context))
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        // 如果是最后一条，不需要刷新view
        if (position == list.size) return
        // 条目数据
        val data = list.get(position)
        // 条目view
        val itemView = holder.itemView as HomeItemView
        // 条目刷新
        itemView.setData(data)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size) {
            // 最后一条
            return 1
        } else {
            // 普通条目
            return 0
        }
    }

}