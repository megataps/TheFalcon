package com.megalabs.themovie.data

import com.megalabs.themovie.model.MovieModel
import io.reactivex.Single


interface MovieRepository {

    fun getMovieDetail(): Single<MovieModel>

}