package com.megalabs.falcon.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.StatFs
import android.util.Log
import java.io.*
import java.net.URLEncoder
import java.util.*
import kotlin.collections.LinkedHashMap

class SimpleDiskLruCache internal constructor(
    private val mCacheDir: File,
    private val maxCacheByteSize: Long
){

    private var cacheSize = 0
    private var cacheByteSize = 0
    private val maxCacheItemSize = 500

    private var mCompressFormat = Bitmap.CompressFormat.JPEG
    private var mCompressQuality = 70
    private val mLinkedHashMap: MutableMap<String, String> = Collections.synchronizedMap(
        LinkedHashMap<String, String>(
            INITIAL_CAPACITY,
            LOAD_FACTOR, true)
    )

    companion object {
        private const val TAG = "SimpleDiskLruCache"
        private const val CACHE_FILENAME_PREFIX = "cache_"
        private const val MAX_REMOVALS = 4
        private const val INITIAL_CAPACITY = 32
        private const val LOAD_FACTOR = 0.75f
        private const val IO_BUFFER_SIZE = 8 * 1024

        /**
         * A filename filter to use to identify the cache filenames which have CACHE_FILENAME_PREFIX
         * prepended.
         */
        private val cacheFileFilter = FilenameFilter { _, filename ->
            filename.startsWith(CACHE_FILENAME_PREFIX)
        }

        private fun getUsableSpace(path: File): Long {
            val statFs = StatFs(path.absolutePath)
            return statFs.blockCountLong * statFs.blockSizeLong
        }

        fun openCache(cacheDir: File, maxByteSize: Long): SimpleDiskLruCache? {
            if (!cacheDir.exists()) {
                cacheDir.mkdir()
            }
            return if (cacheDir.isDirectory &&
                cacheDir.canWrite() &&
                (getUsableSpace(
                    cacheDir
                ) > maxByteSize)
            ) {
                SimpleDiskLruCache(
                    cacheDir,
                    maxByteSize
                )
            } else null
        }
    }

    fun put(key: String, data: Bitmap) {
        synchronized(mLinkedHashMap) {
            if (mLinkedHashMap[key] == null) {
                try {
                    val file = createFilePath(mCacheDir, key)
                    if (writeBitmapToFile(data, file)) {
                        put(key, file!!)
                        flushCache()
                    }
                } catch (e: FileNotFoundException) {
                    Log.e(TAG, "Error in put: " + e.message)
                } catch (e: IOException) {
                    Log.e(TAG, "Error in put: " + e.message)
                }
            }
        }
    }

    private fun put(key: String, file: String) {
        mLinkedHashMap[key] = file
        cacheSize = mLinkedHashMap.size
        cacheByteSize += File(file).length().toInt()
    }

    private fun flushCache() {
        var eldestEntry: Map.Entry<String, String?>
        var eldestFile: File
        var eldestFileSize: Long
        var count = 0
        while (count < MAX_REMOVALS &&
            (cacheSize > maxCacheItemSize || cacheByteSize > maxCacheByteSize)
        ) {
            eldestEntry = mLinkedHashMap.entries.iterator().next()
            eldestFile = File(eldestEntry.value)
            eldestFileSize = eldestFile.length()
            mLinkedHashMap.remove(eldestEntry.key)
            eldestFile.delete()
            cacheSize = mLinkedHashMap.size
            cacheByteSize -= eldestFileSize.toInt()
            count++
        }
    }

    operator fun get(key: String): Bitmap? {
        synchronized(mLinkedHashMap) {
            val file = mLinkedHashMap[key]
            if (file != null) {
                return BitmapFactory.decodeFile(file)
            } else {
                val existingFile = createFilePath(mCacheDir, key)
                if (existingFile != null && File(existingFile).exists()) {
                    put(key, existingFile)
                    return BitmapFactory.decodeFile(existingFile)
                }
            }
            return null
        }
    }

    fun clearCache() {
        clearCache(mCacheDir)
    }

    private fun clearCache(context: Context, uniqueName: String) {
        val cacheDir: File = getDiskCacheDir(context, uniqueName)
        clearCache(cacheDir)
    }

    private fun clearCache(cacheDir: File) {
        val files: Array<File>? = cacheDir.listFiles(cacheFileFilter)
        files?.let{
            for (i in it.indices) {
                files[i].delete()
            }
        }

    }

    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath: String = if (Environment.getExternalStorageState() === Environment.MEDIA_MOUNTED ||
            !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir?.path ?: ""
        } else {
            context.cacheDir.path
        }

        return File(cachePath + File.separator.toString() + uniqueName)
    }

    private fun createFilePath(cacheDir: File?, key: String): String? {
        try {
            // Use URLEncoder to ensure we have a valid filename, a tad hacky but it will do for
            // this example
            return cacheDir?.absolutePath + File.separator.toString() +
                    CACHE_FILENAME_PREFIX + URLEncoder.encode(key.replace("*", ""), "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "createFilePath - $e")
        }
        return null
    }


    private fun setCompressParams(compressFormat: Bitmap.CompressFormat, quality: Int) {
        mCompressFormat = compressFormat
        mCompressQuality = quality
    }

    @Throws(IOException::class, FileNotFoundException::class)
    private fun writeBitmapToFile(bitmap: Bitmap, file: String?): Boolean {
        file?.let {
            var out: OutputStream? = null
            return try {
                out = BufferedOutputStream(
                    FileOutputStream(it),
                    IO_BUFFER_SIZE
                )
                bitmap.compress(mCompressFormat, mCompressQuality, out)
            } finally {
                out?.close()
            }
        }

        return false
    }
}