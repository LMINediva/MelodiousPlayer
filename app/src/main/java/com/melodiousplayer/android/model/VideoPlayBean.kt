package com.melodiousplayer.android.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 传递给视频播放界面的bean类
 */
data class VideoPlayBean(
    var id: Int,
    var title: String,
    var url: String,
    var thumbnailPic: String,
    var description: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeString(thumbnailPic)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoPlayBean> {
        override fun createFromParcel(parcel: Parcel): VideoPlayBean {
            return VideoPlayBean(parcel)
        }

        override fun newArray(size: Int): Array<VideoPlayBean?> {
            return arrayOfNulls(size)
        }
    }

}