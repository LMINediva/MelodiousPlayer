package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.MVAreasContract
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.net.MVAreasRequest
import com.melodiousplayer.android.net.ResponseHandler

class MVAreasPresenterImpl(val view: MVAreasContract.View) : MVAreasContract.Presenter,
    ResponseHandler<List<MVAreaBean>> {

    override fun getMVAreas() {
        MVAreasRequest(this).execute()
    }

    override fun onSuccess(type: Int, result: List<MVAreaBean>) {
        if (result.isNotEmpty()) {
            view.onGetMVAreasSuccess(result)
        } else {
            view.onGetMVAreasFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        view.onNetworkError()
    }

}