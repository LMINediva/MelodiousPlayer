package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.widget.LoadMoreView
import com.melodiousplayer.android.widget.MusicListItemView

/**
 * 悦单界面适配器
 */
class MusicListAdapter : RecyclerView.Adapter<MusicListAdapter.MusicListHolder>() {

    private var list = ArrayList<MusicListBean.PlayListsBean>()

    /**
     * 更新列表
     */
    fun updateList(list: List<MusicListBean.PlayListsBean>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun loadMore(list: List<MusicListBean.PlayListsBean>?) {
        list?.let {
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    class MusicListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListHolder {
        if (viewType == 1) {
            // 刷新控件
            return MusicListHolder(LoadMoreView(parent.context))
        } else {
            // 普通条目
            return MusicListHolder(MusicListItemView(parent.context))
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: MusicListHolder, position: Int) {
        // 最后一条加载更多条目不做处理
        if (position == list.size) return
        // 获取当前条目的数据
        val data = list.get(position)
        // 获取当前条目的视图
        val itemView = holder.itemView as MusicListItemView
        // 当前条目的数据和视图的绑定
        itemView.setData(data)
    }

    /**
     * 获取不同的条目的view样式
     */
    override fun getItemViewType(position: Int): Int {
        if (position == list.size) {
            // 最后一条，为刷新控件
            return 1
        } else {
            // 普通条目控件
            return 0
        }
    }

}