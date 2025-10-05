package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.model.VideosBean

class PagingAdapter(
    private val onItemSelected: (VideosBean, Boolean) -> Unit
) : ListAdapter<VideosBean, PagingAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


    class DiffCallback : DiffUtil.ItemCallback<VideosBean>() {

        override fun areItemsTheSame(oldItem: VideosBean, newItem: VideosBean): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideosBean, newItem: VideosBean): Boolean {
            return oldItem == newItem
        }

    }

}