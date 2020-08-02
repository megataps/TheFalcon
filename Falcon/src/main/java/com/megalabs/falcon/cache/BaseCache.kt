package com.megalabs.falcon.cache

import android.graphics.Bitmap

interface BaseCache {

    fun set(url: String, bitmap: Bitmap)

    fun get(url: String): Bitmap?

    fun clearCache()
}