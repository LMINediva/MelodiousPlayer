package com.melodiousplayer.android.extension

/**
 * 校验用户输入用户名是否有效
 */
fun String.isValidUserName(): Boolean = this.length in 3..20

/**
 * 校验用户输入密码是否有效
 */
fun String.isValidPassword(): Boolean = this.matches(Regex("[\\w\\W]{3,20}$"))