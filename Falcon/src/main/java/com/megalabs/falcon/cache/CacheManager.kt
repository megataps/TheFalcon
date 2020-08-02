package com.megalabs.falcon.cache

import android.content.Context
import android.graphics.Bitmap
import com.megalabs.falcon.FalconConfig

class CacheManager(
    private val context: Context,
    private val config: FalconConfig
): BaseCache {

    private val diskCache = DiskCache(context)
    private val memoryCache = MemoryCache(config)

    override fun set(url: String, bitmap: Bitmap) {
        memoryCache.set(url, bitmap)
        val fileNameGenerated = config.fileNamGenerator.generate(url)
        diskCache.set(fileNameGenerated, bitmap)
    }

    override fun get(url: String): Bitmap? {
        val fileNameGenerated = config.fileNamGenerator.generate(url)
        return memoryCache.get(url) ?: diskCache.get(fileNameGenerated)
    }

    override fun clearCache() {
        memoryCache.clearCache()
        diskCache.clearCache()
    }
}