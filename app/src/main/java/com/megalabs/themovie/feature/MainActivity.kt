package com.megalabs.themovie.feature

import android.os.Bundle
import com.megalabs.falcon.Falcon
import com.megalabs.falcon.ImageLoadingProgressListener
import com.megalabs.themovie.DemoApplication
import com.megalabs.themovie.R
import com.megalabs.themovie.extension.with
import com.megalabs.themovie.model.MovieModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val movieService = DemoApplication.getServiceFactory().getMovieService()
    private var mMovie: MovieModel? = null
    private var mCurrentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thumbnailImageView.setOnClickListener {
            mMovie?.let {
                if (mCurrentIndex < it.images.size - 1) {
                    mCurrentIndex++
                } else {
                    mCurrentIndex = 0
                }

                thumbnailImageView.setImageBitmap(null)
                val url = it.images[mCurrentIndex].imageUrl
                displayImage(url)
            }
        }
    }

    private fun binData() {
        titleTextView.text = mMovie?.title

        mMovie?.let {
            if (mCurrentIndex < it.images.size) {
                val url = it.images[mCurrentIndex].imageUrl
                displayImage(url)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        disposables.add(
            movieService.getMovieDetail().with()
                .subscribeBy (
                    onError = {
                        it.printStackTrace()
                    },
                    onSuccess = {
                        mMovie = it
                        binData()
                    }
                )
        )
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun displayImage(url: String) {
        Falcon.getInstance().loadImage(url, thumbnailImageView, R.drawable.loading_animation, 800, 800,
            object: ImageLoadingProgressListener {
                override fun onProgressUpdate(url: String, percent: Int, downloaded: Int, total: Int) {
                    runOnUiThread {
                        if (downloaded == total) {
                            downloadPercentageTextView.text = ""
                        } else {
                            downloadPercentageTextView.text = "${downloaded / 1024} kb downloading"
                        }
                    }
                }
            })
    }
}