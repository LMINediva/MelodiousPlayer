package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UserBean

interface UpdateAvatarContract {

    interface Presenter : BasePresenter {
        fun updateAvatar(token: String, user: UserBean)
    }

    interface View {
        fun onUpdateAvatarSuccess()
        fun onUpdateAvatarFailed()
        fun onNetworkError()
    }

}