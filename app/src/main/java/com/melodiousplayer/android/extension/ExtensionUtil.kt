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
 * 校验用户输入手机号码是否有效
 */
fun String.isValidPhoneNumber(): Boolean = this.matches(Regex("^1[3456789]\\d{9}\$"))

/**
 * 校验用户输入邮箱地址是否有效
 */
fun String.isValidEmail(): Boolean =
    this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))

/**
 * 校验用户输入悦单类型是否有效
 */
fun String.isValidCategory(): Boolean = this.length in 2..10