package com.megalabs.falcon

import android.os.Process
import com.megalabs.falcon.naming.FileNamGenerator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class FalconConfig(
    val fileNamGenerator: FileNamGenerator,
    val isEnableCache: Boolean = true,
    val delayTimeForDisplay: Long = 1000
) {
    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    // Use 1/8th of the available memory for this memory cache.
    val cacheSize = maxMemory / 8

    val executorService: ExecutorService

    init {
        executorService = Executors.newFixedThreadPool(5, ImageThreadFactory())
    }

    internal class ImageThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "Falcon Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }
}