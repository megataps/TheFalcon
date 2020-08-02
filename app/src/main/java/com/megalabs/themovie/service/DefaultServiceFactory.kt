package com.megalabs.themovie.service

import com.megalabs.themovie.data.MovieRepository
import com.megalabs.themovie.data.MovieRepositoryImpl
import com.megalabs.themovie.model.MovieModel
import com.megalabs.themovie.parser.MovieParser
import com.megalabs.themovie.parser.Parser

/**
 * This class is responsible for init object of Service and Data Repository layer. It likes DI container for wiring
 * object. This class is called in Application class as application start.
 *
 */

class DefaultServiceFactory: ServiceFactory {

    private var movieParser: Parser<MovieModel> = MovieParser()
    private var movieRepository: MovieRepository = MovieRepositoryImpl(movieParser)
    private var movieService: MovieService = MovieServiceImpl(movieRepository)

    override fun getMovieService(): MovieService {
        return movieService
    }
}