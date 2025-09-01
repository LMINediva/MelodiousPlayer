package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.LoginContract
import com.melodiousplayer.android.extension.isValidPassword
import com.melodiousplayer.android.extension.isValidUserName
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.net.LoginRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.util.ThreadUtil
import com.melodiousplayer.android.util.URLProviderUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginPresenterImpl(val view: LoginContract.View) : LoginContract.Presenter,
    ResponseHandler<UserResultBean> {

    private val client by lazy { OkHttpClient() }

    override fun login(userName: String, password: String) {
        if (userName.isValidUserName()) {
            // 用户名合法，继续校验密码
            if (password.isValidPassword()) {
                // 密码合法，开始登录
                view.onStartLogin()
                // 登录到后端服务器
                loginRequest(userName, password)
            } else {
                view.onPasswordError()
            }
        } else {
            view.onUserNameError()
        }
    }

    private fun compareVerificationCodeRequest(verificationCode: String) {
        val jsonObject = JSONObject()
        jsonObject.put("code", verificationCode)
        val requestBody: RequestBody =
            RequestBody.create(
                "application/json;charset=utf-8".toMediaType(),
                jsonObject.toString()
            )
        val request = Request.Builder()
            .url(URLProviderUtils.postCompareVerificationCode())
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            /**
             * 子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread {
                    // 回调到view层处理
                    view.onNetworkError()
                }
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
//                val parseResult = req.parseResult(result)
                ThreadUtil.runOnMainThread {
//                    req.handler.onSuccess(req.type, parseResult)
                }
            }
        })
    }

    private fun loginRequest(userName: String, password: String) {
        LoginRequest(userName, password, this).executePost(null)
    }

    override fun onSuccess(type: Int, result: UserResultBean) {
        if (result.code == 200) {
            view.onLoginSuccess(result)
        } else {
            view.onLoginFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        view.onNetworkError()
    }

}