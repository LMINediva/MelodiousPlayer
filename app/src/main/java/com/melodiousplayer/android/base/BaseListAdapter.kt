package com.melodiousplayer.android.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.widget.LoadMoreView

/**
 * 所有下拉刷新和上拉加载更多列表界面adapter基类
 */
abstract class BaseListAdapter<ITEMBEAN, ITEMVIEW : View> :
    RecyclerView.Adapter<BaseListAdapter.BaseListHolder>() {

    private var list = ArrayList<ITEMBEAN>()

    class BaseListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    /**
     * 更新数据
     */
    fun updateList(list: List<ITEMBEAN>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    /**
     * 加载更多
     */
    fun loadMoreList(list: List<ITEMBEAN>?) {
        list?.let {
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListHolder {
        if (viewType == 1) {
            // 最后一条
            return BaseListHolder(LoadMoreView(parent.context))
        } else {
            // 普通条目
            return BaseListHolder(getItemView(parent.context))
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: BaseListHolder, position: Int) {
        // 如果是最后一条，不需要刷新view
        if (position == list.size) return
        // 条目数据
        val data = list.get(position)
        // 条目view
        val itemView = holder.itemView as ITEMVIEW
        // 条目刷新
        refreshItemView(itemView, data)
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

    /**
     * 刷新条目view
     */
    abstract fun refreshItemView(itemView: ITEMVIEW, data: ITEMBEAN)

    /**
     * 获取条目view
     */
    abstract fun getItemView(context: Context?): ITEMVIEW

}