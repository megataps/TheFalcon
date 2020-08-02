package com.megalabs.themovie.service

import com.megalabs.themovie.model.MovieModel
import io.reactivex.Single

interface MovieService {

    fun getMovieDetail(): Single<MovieModel>
}