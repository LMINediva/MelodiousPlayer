package com.melodiousplayer.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 悦单中的MV列表适配器
 */
class MusicListInformationAdapter(val mvList: List<VideosBean>) :
    RecyclerView.Adapter<MusicListInformationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val artist: TextView = view.findViewById(R.id.artist)
        val title: TextView = view.findViewById(R.id.title)
        val bg: ImageView = view.findViewById(R.id.bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mv = mvList[position]
        // 歌手名称
        holder.artist.text = mv.artistName
        // MV名称
        holder.title.text = mv.title
        // 背景图
        Glide.with(holder.itemView.context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.mvImagePath + mv.posterPic
        ).into(holder.bg)
    }

    override fun getItemCount(): Int {
        return mvList.size
    }

}