package com.megalabs.falcon

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class IOUtils {

    companion object {
        const val BYTE_LENGTH_DEFAULT = 1024

        fun bytesToString(data: ByteArray?): String {
            return String(data!!, StandardCharsets.UTF_8)
        }
    }
}