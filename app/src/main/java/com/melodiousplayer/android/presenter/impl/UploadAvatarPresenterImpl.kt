package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadAvatarContract
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadAvatarRequest
import java.io.File

class UploadAvatarPresenterImpl(val view: UploadAvatarContract.View) : UploadAvatarContract.Presenter,
    ResponseHandler<UploadAvatarRequest> {

    override fun uploadImage(file: File) {
        TODO("Not yet implemented")
    }

    override fun onError(type: Int, msg: String?) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(type: Int, result: UploadAvatarRequest) {
        TODO("Not yet implemented")
    }

}