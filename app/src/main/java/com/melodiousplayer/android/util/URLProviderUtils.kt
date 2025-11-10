package com.melodiousplayer.android.util

import android.os.Build
import android.util.Log

/**
 * 应用所有网络请求的URL工具类
 */
object URLProviderUtils {

    const val protocol = "http://"
    var serverAddress = "192.168.124.3:8082"
    const val musicImagePath = "/image/musicPicture/"
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
     * 获取相关区域的MV数据列表的url
     *
     * @param area   区域数据类型
     * @param offset 数据偏移量
     * @param size   返回数据的条目个数
     * @return url
     */
    fun getMVUrl(area: String?, offset: Int, size: Int): String {
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

    /**
     * 修改用户登录密码
     */
    fun postChangePassword(): String {
        val url = protocol + serverAddress + "/sys/user/updateUserPwd"
        return url
    }

    /**
     * 上传音乐相关图片
     */
    fun postUploadMusicPicture(): String {
        val url = protocol + serverAddress + "/data/music/uploadImage"
        return url
    }

    /**
     * 上传歌词文件
     */
    fun postUploadLyric(): String {
        val url = protocol + serverAddress + "/data/music/uploadLyric"
        return url
    }

    /**
     * 上传音乐文件
     */
    fun postUploadMusic(): String {
        val url = protocol + serverAddress + "/data/music/uploadAudio"
        return url
    }

    /**
     * 检查在线音乐名是否存在
     */
    fun postCheckMusicTitle(): String {
        val url = protocol + serverAddress + "/data/music/checkTitle"
        return url
    }

    /**
     * 添加音乐
     */
    fun postAddMusic(): String {
        val url = protocol + serverAddress + "/data/music/save"
        return url
    }

    /**
     * 获取歌词文本
     */
    fun getLyricText(lyricFileName: String): String {
        val url = protocol + serverAddress + lyricPath + lyricFileName
        return url
    }

    /**
     * 删除音乐
     */
    fun postDeleteMusic(): String {
        val url = protocol + serverAddress + "/data/music/delete"
        return url
    }

    /**
     * 删除上传到服务器的音乐相关文件缓存
     */
    fun postDeleteUploadMusicFileCache(): String {
        val url = protocol + serverAddress + "/data/music/deleteUploadFileCache"
        return url
    }

    /**
     * MV区域请求
     */
    fun getMVAreas(): String {
        val url = protocol + serverAddress + "/mv_areas"
        return url
    }

    /**
     * 上传MV相关图片
     */
    fun postUploadMVPicture(): String {
        val url = protocol + serverAddress + "/data/mv/uploadImage"
        return url
    }

    /**
     * 上传MV文件
     */
    fun postUploadMV(): String {
        val url = protocol + serverAddress + "/data/mv/uploadVideo"
        return url
    }

    /**
     * 删除上传到服务器的MV相关文件缓存
     */
    fun postDeleteUploadMVFileCache(): String {
        val url = protocol + serverAddress + "/data/mv/deleteUploadFileCache"
        return url
    }

    /**
     * 检查在线MV名是否存在
     */
    fun postCheckMVTitle(): String {
        val url = protocol + serverAddress + "/data/mv/checkTitle"
        return url
    }

    /**
     * 添加MV
     */
    fun postAddMV(): String {
        val url = protocol + serverAddress + "/data/mv/save"
        return url
    }

    /**
     * 分页获取MV数据列表的url
     */
    fun postPagingMVUrl(): String {
        val url = protocol + serverAddress + "/data/mv/list"
        return url
    }

    /**
     * 上传悦单相关图片
     */
    fun postUploadMusicListPicture(): String {
        val url = protocol + serverAddress + "/data/list/uploadImage"
        return url
    }

    /**
     * 检查在线悦单名是否存在
     */
    fun postCheckMusicListTitle(): String {
        val url = protocol + serverAddress + "/data/list/checkTitle"
        return url
    }

    /**
     * 添加悦单
     */
    fun postAddMusicList(): String {
        val url = protocol + serverAddress + "/data/list/save"
        return url
    }

    /**
     * 删除上传到服务器的悦单相关文件缓存
     */
    fun postDeleteUploadMusicListFileCache(): String {
        val url = protocol + serverAddress + "/data/list/deleteUploadFileCache"
        return url
    }

    /**
     * 分页获取我的音乐数据列表的url
     */
    fun postPagingMyMusicUrl(): String {
        val url = protocol + serverAddress + "/data/music/myList"
        return url
    }

    /**
     * 分页获取我的MV数据列表的url
     */
    fun postPagingMyMVUrl(): String {
        val url = protocol + serverAddress + "/data/mv/myList"
        return url
    }

    /**
     * 分页获取我的悦单数据列表的url
     */
    fun postPagingMyMusicListUrl(): String {
        val url = protocol + serverAddress + "/data/list/myList"
        return url
    }

    /**
     * APK检查更新的url
     */
    fun postCheckUpdateUrl(): String {
        val url = protocol + serverAddress + "/sys/android/checkUpdate"
        return url
    }

    /**
     * APK更新下载的url
     */
    fun getAPKUpdateUrl(): String {
        val url = protocol + serverAddress + "/sys/android/downloadAPK/"
        return url
    }

    /**
     * 上传用户反馈图片
     */
    fun postUploadFeedBackPicture(): String {
        val url = protocol + serverAddress + "/sys/feedback/uploadImage"
        return url
    }

    /**
     * 添加用户反馈
     */
    fun postAddFeedBack(): String {
        val url = protocol + serverAddress + "/sys/feedback/save"
        return url
    }

    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getPhoneModel(): String {
        return Build.MODEL
    }

}
