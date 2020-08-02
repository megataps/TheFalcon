package com.megalabs.falcon

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.megalabs.falcon.cache.CacheManager
import java.util.concurrent.Future

class Falcon(
    private val context: Context,
    private val config: FalconConfig
) {
    private val cacheManager = CacheManager(context, config)
    private val mImageLoaderTasks:HashMap<String, Future<Bitmap?>> = hashMapOf()

    fun loadImage(url: String, imageView: ImageView, placeHolder: Int?, width: Int, height: Int,
                  listener: ImageLoadingProgressListener) {
        var bitmap: Bitmap? = null
        if (config.isEnableCache) {
            bitmap = cacheManager.get(url)
        }

        bitmap?.let {
            imageView.setImageBitmap(it)
            return
        } ?: run {
            imageView.tag = url

            if (placeHolder != null) {
                imageView.setImageResource(placeHolder)
            }

            if (config.isEnableCache) {
                addImageLoaderTask(url, ImageLoader(url, imageView, cacheManager, width, height, listener, config.delayTimeForDisplay))
            } else {
                addImageLoaderTask(url, ImageLoader(url, imageView, null, width, height, listener, config.delayTimeForDisplay))
            }
        }
    }

    private fun addImageLoaderTask(url: String, imageLoader: ImageLoader) {
        mImageLoaderTasks[url] = config.executorService.submit(imageLoader)
    }

    fun clearCache() {
        cacheManager.clearCache()
    }

    fun cancelTask(url: String){
        synchronized(this){
            mImageLoaderTasks.forEach {
                if (it.key == url &&  !it.value.isDone)
                    it.value.cancel(true)
            }
        }
    }

    fun  cancelAllTasks() {
        synchronized (this) {
            mImageLoaderTasks.forEach{
                if ( !it.value.isDone)
                    it.value.cancel(true)
            }
            mImageLoaderTasks.clear()
        }
    }

    companion object {

        private var INSTANCE: Falcon? = null

        @Synchronized
        fun init(context: Context, config: FalconConfig) {
            INSTANCE = Falcon(context, config)
        }

        @Synchronized
        fun getInstance(): Falcon {
            return INSTANCE!!
        }
    }
}