package com.melodiousplayer.android.model

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore.Audio.Media

/**
 * 音乐列表条目bean
 */
data class AudioBean(
    var data: String, var size: Long, var displayName: String, var artist: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(data)
        parcel.writeLong(size)
        parcel.writeString(displayName)
        parcel.writeString(artist)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AudioBean> {

        override fun createFromParcel(parcel: Parcel): AudioBean {
            return AudioBean(parcel)
        }

        override fun newArray(size: Int): Array<AudioBean?> {
            return arrayOfNulls(size)
        }

        /**
         * 根据特定位置上的cursor获取bean
         */
        fun getAudioBean(cursor: Cursor?): AudioBean {
            // 创建AudioBean对象
            val audioBean = AudioBean("", 0, "", "")
            // 判断Cursor是否为空
            cursor?.let {
                // 解析Cursor并且设置到Bean对象中
                audioBean.data = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
                audioBean.size = cursor.getLong(cursor.getColumnIndexOrThrow(Media.SIZE))
                audioBean.displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME))
                audioBean.displayName =
                    audioBean.displayName.substring(0, audioBean.displayName.lastIndexOf("."))
                audioBean.artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
            }
            return audioBean
        }

        /**
         * 根据特定位置cursor获取整个播放列表
         */
        fun getAudioBeans(cursor: Cursor?): ArrayList<AudioBean> {
            // 创建集合
            val list = ArrayList<AudioBean>()
            // cursor是否为空
            cursor?.let {
                // 将cursor游标移动到-1
                it.moveToPosition(-1)
                // 解析cursor添加到集合中
                while (it.moveToNext()) {
                    val audioBean = getAudioBean(it)
                    list.add(audioBean)
                }
            }
            return list
        }

    }

}