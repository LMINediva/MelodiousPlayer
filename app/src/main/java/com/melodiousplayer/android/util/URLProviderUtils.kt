package com.melodiousplayer.android.util

import android.os.Build
import android.util.Log

/**
 * 应用所有网络请求的URL工具类
 */
object URLProviderUtils {

    const val protocol = "http://"
    var serverAddress = "192.168.124.3:8082"
    const val imagePath = "/image/musicPicture/"
    const val musicPath = "/audio/music/"
    const val lyricPath = "/audio/lyric/"
    const val mvImagePath = "/image/mvPicture/"
    const val mvPath = "/video/mv/"
    const val listPicturePath = "/image/listPicture/"
    const val userAvatarPath = "/image/userAvatar/"

    /**
     * 获取首页的url
     *
     * @param offset 数据偏移量
     * @param size   返回数据的条目个数
     * @return url
     */
    fun getHomeUrl(offset: Int, size: Int): String {
        val url =
            protocol + serverAddress + "/data/music/front_page" + "?offset=" + offset + "&size=" + size
        Log.i("Main_url", url)
        Log.i("offset", offset.toString())
        Log.i("size", size.toString())
        return url
    }

    /**
     * 获取MV界面区域数据的url
     *
     * @return url
     */
    fun getMVAreaUrl(): String {
        val url = protocol + serverAddress + "/get_mv_areas"
        return url
    }

    /**
     * 获取MV界面区域数据列表的url
     *
     * @param area   区域数据类型
     * @param offset 数据偏移量
     * @param size   返回数据的条目个数
     * @return url
     */
    fun getMVAreaListUrl(area: String?, offset: Int, size: Int): String {
        val url = protocol + serverAddress + "/data/mv/get_mv_list" +
                "?offset=" + offset + "&size=" + size + "&area=" + area
        return url
    }

    /**
     * 获取悦单界面数据的url
     *
     * @param offset 数据偏移量
     * @param size   返回数据的条目个数
     * @return url
     */
    fun getMVListUrl(offset: Int, size: Int): String {
        val url =
            protocol + serverAddress + "/data/list/get_list" + "?offset=" + offset + "&size=" + size
        Log.i("offset", offset.toString())
        Log.i("size", size.toString())
        return url
    }

    /**
     * 用户登录请求url
     */
    fun postLoginUrl(userName: String, password: String): String {
        val url =
            protocol + serverAddress + "/login?" + "username=" + userName + "&password=" + password
        return url
    }

    /**
     * 用户Token登录获取用户信息url
     */
    fun postTokenLoginUrl(): String {
        val url =
            protocol + serverAddress + "/userInfo"
        return url
    }

    /**
     * 用户注册请求url
     */
    fun postRegisterUrl(): String {
        val url = protocol + serverAddress + "/register"
        return url
    }

    /**
     * 用户退出登录请求url
     */
    fun getLogoutUrl(): String {
        val url = protocol + serverAddress + "/logout"
        return url
    }

    /**
     * 获取验证码
     */
    fun getVerificationCode(): String {
        val url = protocol + serverAddress + "/captcha"
        return url
    }

    /**
     * 比较验证码
     */
    fun postCompareVerificationCode(): String {
        val url = protocol + serverAddress + "/compareCode"
        return url
    }

    /**
     * 上传头像
     */
    fun postUploadAvatar(): String {
        val url = protocol + serverAddress + "/sys/user/uploadImage"
        return url
    }

    /**
     * 更新头像
     */
    fun postUpdateAvatar(): String {
        val url = protocol + serverAddress + "/sys/user/updateAvatar"
        return url
    }

    /**
     * 更新用户信息
     */
    fun postUpdateUserInfo(): String {
        val url = protocol + serverAddress + "/sys/user/save"
        return url
    }

    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getPhoneModel(): String {
        return Build.MODEL
    }

}
