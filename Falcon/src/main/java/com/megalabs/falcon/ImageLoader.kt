package com.megalabs.falcon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import com.megalabs.falcon.cache.CacheManager
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable

class ImageLoader(
    private val mUrl: String,
    private val mImageView: ImageView,
    private val cacheManager: CacheManager? = null,
    private val mWidth: Int,
    private val mHeight: Int,
    private val listener: ImageLoadingProgressListener? = null,
    private val delayTimeForDisplay: Long
) : Callable<Bitmap?> {

    private val uiHandler = Handler(Looper.getMainLooper())

    override fun call(): Bitmap? {
        val bitmap = fetchImage(mUrl)
        bitmap?.let {
            if (mImageView.tag == mUrl) {
                bindImageView(mImageView, it)
            }

            cacheManager?.let { cache ->
                cache.set(mUrl, it)
            }
        }
        return bitmap
    }

    private fun fetchImage(imageUrl: String): Bitmap? {
        var bitmap: Bitmap? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(imageUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 1000 * 30
            connection.readTimeout = 1000 * 30


            if (connection.responseCode == HttpURLConnection.HTTP_OK) {

                val byteArrayOutputStream = ByteArrayOutputStream()
                connection.inputStream.copyTo(byteArrayOutputStream)
                val firstClone: InputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                val secondClone: InputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())

                val total: Int = connection.contentLength

                val data = ByteArray(IOUtils.BYTE_LENGTH_DEFAULT)
                var downloaded: Int = 0
                var count: Int

                while (secondClone.read(data).also { count = it } != -1) {
                    downloaded += count
                    val percentage = (downloaded * 100 / total)
                    listener?.onProgressUpdate(imageUrl, percentage as Int, downloaded, total)
                    Thread.sleep(delayTimeForDisplay)
                }


                val bufferedInputStream = BufferedInputStream(firstClone)
                bitmap = scaleBitmap(bufferedInputStream, mWidth, mHeight)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.let {
                connection.disconnect()
            }
        }
        return bitmap
    }

    private fun bindImageView(imageView: ImageView, bitmap: Bitmap) {
        uiHandler.post {
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun scaleBitmap(inputStream: BufferedInputStream, width: Int, height: Int) : Bitmap? {
        return BitmapFactory.Options().run {
            inputStream.mark(inputStream.available())

            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)

            inSampleSize = calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null,  this)
        }
    }

    private fun scaleBitmap(buffer: ByteArray, width: Int, height: Int) : Bitmap? {
        return BitmapFactory.Options().run {

            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(buffer, 0, buffer.size, this)

            inSampleSize = calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false

            BitmapFactory.decodeByteArray(buffer, 0,  buffer.size, this)
        }
    }
}