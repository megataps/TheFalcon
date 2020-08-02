package com.megalabs.falcon.naming

import android.util.Log
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Md5FileNameGenerator: FileNamGenerator {

    companion object{
        private const val TAG = "Md5FileNameGenerator"
        private const val HASH_ALGORITHM = "MD5"
        private const val RADIX = 10 + 26 // 10 digits + 26 letters

    }

    override fun generate(url: String): String {
        val md5 = getMD5(url.toByteArray())
        val bi: BigInteger = BigInteger(md5).abs()
        return bi.toString(RADIX)
    }

    private fun getMD5(data: ByteArray): ByteArray? {
        var hash: ByteArray? = null
        try {
            val digest: MessageDigest = MessageDigest.getInstance(HASH_ALGORITHM)
            digest.update(data)
            hash = digest.digest()
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, e.message, e)
        }
        return hash
    }
}