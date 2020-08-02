package com.megalabs.themovie.service

import com.megalabs.themovie.data.MovieRepository
import com.megalabs.themovie.model.MovieModel
import io.reactivex.Single

class MovieServiceImpl(
    private val movieRepository: MovieRepository
) : MovieService {

    override fun getMovieDetail(): Single<MovieModel> {
        return movieRepository.getMovieDetail()
    }
}