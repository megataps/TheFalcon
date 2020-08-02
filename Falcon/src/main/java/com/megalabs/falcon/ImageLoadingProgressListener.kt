package com.megalabs.falcon

interface ImageLoadingProgressListener {

    fun onProgressUpdate(url: String, percent: Int, downloaded: Int, total: Int)
}