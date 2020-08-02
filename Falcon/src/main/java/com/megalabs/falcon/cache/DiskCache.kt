package com.megalabs.falcon.cache

import android.content.Context
import android.graphics.Bitmap

class DiskCache(
    context: Context
): BaseCache {

    private val maxCacheSize: Long = 10 * 1024 * 1024 // 10MB
    private var diskCache: SimpleDiskLruCache? = SimpleDiskLruCache.openCache(context.cacheDir, maxCacheSize)

    override fun set(url: String, bitmap: Bitmap) {
        diskCache?.put(url, bitmap)
    }

    override fun get(url: String): Bitmap? {
        return diskCache?.get(url)
    }

    override fun clearCache() {
        diskCache?.clearCache()
    }
}