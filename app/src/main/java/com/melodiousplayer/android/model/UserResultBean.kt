package com.melodiousplayer.android.model

data class UserResultBean(
    var msg: String?,
    var code: Int?,
    var authorization: String?,
    var currentUser: UserBean?
)
