package com.melodiousplayer.android.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密和解密工具类
 */
object EncryptUtil {

    private const val AES = "AES"

    // 可以是 128, 192 或 256
    private const val AES_KEY_SIZE = 128
    private const val CHARSET = "UTF-8"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

    // 初始化向量，长度必须是16字节
    private val ivBytes = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

    // 密钥，需要保密且足够复杂，最好是随机生成的字符串或密钥管理服务提供的密钥
    const val key = "secret-key"

    /**
     * 加密
     */
    fun encrypt(data: String, key: String): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = generateKey(key)
            val ivSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encrypted, Base64.DEFAULT) // 将加密后的字节数组转换为Base64编码的字符串
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 解密
     */
    fun decrypt(encryptedData: String, key: String): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = generateKey(key)
            val ivSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
            // 将解密后的字节数组转换为字符串
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateKey(password: String): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        val hash = sha.digest(password.toByteArray(Charsets.UTF_8))
        // 根据密钥长度截取相应长度的字节数组作为密钥
        return SecretKeySpec(hash, 0, AES_KEY_SIZE / 8, AES)
    }

}