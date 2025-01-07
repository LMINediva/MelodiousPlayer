package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.model.MVListBean
import com.melodiousplayer.android.widget.MVListItemView

/**
 * 悦单界面适配器
 */
class MVListAdapter : RecyclerView.Adapter<MVListAdapter.MVListHolder>() {

    private var list = ArrayList<MVListBean.PlayListsBean>()

    /**
     * 更新列表
     */
    fun updateList(list: List<MVListBean.PlayListsBean>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    class MVListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MVListHolder {
        return MVListHolder(MVListItemView(parent?.context))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MVListHolder, position: Int) {
        // 获取当前条目的数据
        val data = list.get(position)
        // 获取当前条目的视图
        val itemView = holder.itemView as MVListItemView
        // 当前条目的数据和视图的绑定
        itemView.setData(data)
    }

}