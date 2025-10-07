package com.melodiousplayer.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * MV列表分页表格适配器
 */
class PagingAdapter(
    private var items: List<VideosBean>,
    private val selectedItems: Set<VideosBean>,
    private val onItemSelected: (VideosBean, Boolean) -> Unit
) : RecyclerView.Adapter<PagingAdapter.ViewHolder>() {

    fun updateItems(newItems: List<VideosBean>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mv_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, selectedItems.contains(item), onItemSelected)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: VideosBean,
            isSelected: Boolean,
            onItemSelected: (VideosBean, Boolean) -> Unit
        ) {
            val title: TextView = itemView.findViewById(R.id.title)
            val artistName: TextView = itemView.findViewById(R.id.artistName)
            val posterPicture: ImageView = itemView.findViewById(R.id.posterPicture)
            val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
            title.text = item.title
            artistName.text = item.artistName
            Glide.with(itemView.context).load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.mvImagePath + item.posterPic
            ).into(posterPicture)
            checkBox.isChecked = isSelected
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onItemSelected(item, isChecked)
            }
        }
    }

}