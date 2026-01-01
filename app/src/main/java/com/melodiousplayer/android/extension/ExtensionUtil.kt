package com.melodiousplayer.android.extension

/**
 * 校验用户输入的用户名是否有效
 */
fun String.isValidUserName(): Boolean = this.length in 2..20

/**
 * 校验用户输入的密码是否有效
 */
fun String.isValidPassword(): Boolean = this.matches(Regex("[\\w\\W]{3,20}$"))

/**
 * 校验用户输入的手机号码是否有效
 */
fun String.isValidPhoneNumber(): Boolean = this.matches(Regex("^1[3456789]\\d{9}\$"))

/**
 * 校验用户输入的邮箱地址是否有效
 */
fun String.isValidEmail(): Boolean =
    this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))

/**
 * 校验用户输入的音乐名是否有效
 */
fun String.isValidTitle(): Boolean = this.length in 1..80

/**
 * 校验用户输入的歌手姓名是否有效
 */
fun String.isValidArtistName(): Boolean = this.length in 1..80

/**
 * 校验用户输入的描述是否有效
 */
fun String.isValidDescription(): Boolean = this.length in 1..220

/**
 * 校验用户输入的悦单类型是否有效
 */
fun String.isValidCategory(): Boolean = this.length in 2..10

/**
 * 校验用户反馈的内容长度是否有效
 */
fun String.isValidFeedBackContent(): Boolean = this.length in 1..200