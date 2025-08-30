package com.melodiousplayer.android.util

import android.os.Build
import android.util.Log

/**
 * 应用所有网络请求的URL工具类
 */
object URLProviderUtils {

    const val protocol = "http://"
    var serverAddress = "192.168.124.2:8082"
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
        val url = protocol + serverAddress + "/addUser"
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
     *
     * 获取音乐节目列表
     *
     * @param artistIds
     * @param offset
     * @param size
     * @return
     */
    fun getYinYueProgramList(artistIds: String, offset: Int, size: Int): String {
        val url = ("http://mapi.yinyuetai.com/playlist/show.json?deviceinfo="
                + "{\"aid\":\"10201036\",\"os\":\"Android\","
                + "\"ov\":" + "\"" + getSystemVersion() + "\"" + ","
                + "\"rn\":\"480*800\","
                + "\"dn\":" + "\"" + getPhoneModel() + "\"" + ","
                + "\"cr\":\"46000\","
                + "\"as\":"
                + "\"WIFI\","
                + "\"uid\":"
                + "\"dbcaa6c4482bc05ecb0bf39dabf207d2\","
                + "\"clid\":110025000}"
                + "&offset=" + offset
                + "&size=" + size
                + "&artistIds=" + artistIds)
        return url
    }

    /**
     * 获取V榜地址
     *
     * @return
     */
    fun getVChartAreasUrl(): String {
        val url = ("http://mapi.yinyuetai.com/vchart/get_vchart_areas.json?deviceinfo="
                + "{\"aid\":\"10201036\",\"os\":\"Android\","
                + "\"ov\":" + "\"" + getSystemVersion() + "\"" + ","
                + "\"rn\":\"480*800\","
                + "\"dn\":" + "\"" + getPhoneModel() + "\"" + ","
                + "\"cr\":\"46000\","
                + "\"as\":"
                + "\"WIFI\","
                + "\"uid\":"
                + "\"dbcaa6c4482bc05ecb0bf39dabf207d2\","
                + "\"clid\":110025000}")
        return url
    }

    /**
     * 获取V榜的周期
     *
     * @return
     */
    fun getVChartPeriodUrl(area: String): String {
        val url = ("http://mapi.yinyuetai.com/vchart/period.json?deviceinfo="
                + "{\"aid\":\"10201036\",\"os\":\"Android\","
                + "\"ov\":" + "\"" + getSystemVersion() + "\"" + ","
                + "\"rn\":\"480*800\","
                + "\"dn\":" + "\"" + getPhoneModel() + "\"" + ","
                + "\"cr\":\"46000\","
                + "\"as\":"
                + "\"WIFI\","
                + "\"uid\":"
                + "\"dbcaa6c4482bc05ecb0bf39dabf207d2\","
                + "\"clid\":110025000}"
                + "&area=" + area)
        return url
    }

    /**
     * 获取V榜列表
     *
     * @param area
     * @param dateCode
     * @return
     */
    fun getVChartListUrl(area: String, dateCode: Int): String {
        val url = ("http://mapi.yinyuetai.com/vchart/show.json?deviceinfo="
                + "{\"aid\":\"10201036\",\"os\":\"Android\","
                + "\"ov\":" + "\"" + getSystemVersion() + "\"" + ","
                + "\"rn\":\"480*800\","
                + "\"dn\":" + "\"" + getPhoneModel() + "\"" + ","
                + "\"cr\":\"46000\","
                + "\"as\":"
                + "\"WIFI\","
                + "\"uid\":"
                + "\"dbcaa6c4482bc05ecb0bf39dabf207d2\","
                + "\"clid\":110025000}"
                + "&area=" + area
                + "&datecode=" + dateCode)
        return url
    }

    /**
     * 获取相关MV
     *
     * @param id
     * @return
     */
    fun getRelativeVideoListUrl(id: Int): String {
        val url = ("http://mapi.yinyuetai.com/video/show.json?deviceinfo="
                + "{\"aid\":\"10201036\",\"os\":\"Android\","
                + "\"ov\":" + "\"" + getSystemVersion() + "\"" + ","
                + "\"rn\":\"480*800\","
                + "\"dn\":" + "\"" + getPhoneModel() + "\"" + ","
                + "\"cr\":\"46000\","
                + "\"as\":"
                + "\"WIFI\","
                + "\"uid\":"
                + "\"dbcaa6c4482bc05ecb0bf39dabf207d2\","
                + "\"clid\":110025000}"
                + "&relatedVideos=true"
                + "&id=" + id)
        return url
    }

    /**
     * 通过id 获取某人的悦单
     *
     * @param id
     * @return
     */
    fun getPeopleYueDanList(id: Int): String {
        val url = ("http://mapi.yinyuetai.com/playlist/show.json?deviceinfo="
                + "{\"aid\":\"10201036\",\"os\":\"Android\","
                + "\"ov\":" + "\"" + getSystemVersion() + "\"" + ","
                + "\"rn\":\"480*800\","
                + "\"dn\":" + "\"" + getPhoneModel() + "\"" + ","
                + "\"cr\":\"46000\","
                + "\"as\":"
                + "\"WIFI\","
                + "\"uid\":"
                + "\"dbcaa6c4482bc05ecb0bf39dabf207d2\","
                + "\"clid\":110025000}"
                + "&id=" + id)
        return url
    }

    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getPhoneModel(): String {
        return Build.MODEL
    }

}
