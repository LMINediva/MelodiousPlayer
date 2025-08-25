package com.melodiousplayer.android.model

import java.util.Date

data class UserBean(
    var id: Int?,
    var username: String?,
    var password: String?,
    var avatar: String?,
    var email: String?,
    var phonenumber: String?,
    var loginDate: Date?,
    var status: String?,
    var createTime: Date?,
    var updateTime: Date?,
    var remark: String?,
)