package com.melodiousplayer.android.extension

/**
 * 校验用户输入用户名是否有效
 */
fun String.isValidUserName(): Boolean = this.length in 3..20

/**
 * 校验用户输入密码是否有效
 */
fun String.isValidPassword(): Boolean = this.matches(Regex("[\\w\\W]{3,20}$"))

/**
 * 检验用户输入手机号码是否有效
 */
fun String.isValidPhoneNumber(): Boolean = this.matches(Regex("^1[3456789]\\d{9}\$"))