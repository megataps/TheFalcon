package com.megalabs.falcon.cache

import android.graphics.Bitmap
import androidx.collection.LruCache
import com.megalabs.falcon.FalconConfig

class MemoryCache(
    val config: FalconConfig
): BaseCache {

    private val memoryCache: LruCache<String, Bitmap>

    init {
        memoryCache = object : LruCache<String, Bitmap>(config.cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    override fun set(url: String, bitmap: Bitmap) {
        memoryCache.put(url, bitmap)
    }

    override fun get(url: String): Bitmap? {
        return memoryCache.get(url)
    }

    override fun clearCache() {
        memoryCache.evictAll()
    }
}