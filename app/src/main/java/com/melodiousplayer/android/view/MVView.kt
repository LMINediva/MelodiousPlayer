package com.melodiousplayer.android.view

import com.melodiousplayer.android.model.MVAreaBean

interface MVView {

    fun onError(msg: String?)

    fun onSuccess(result: List<MVAreaBean>)

}