package com.megalabs.themovie.data

import com.megalabs.themovie.model.MovieModel
import com.megalabs.themovie.parser.Parser
import io.reactivex.Single

class MovieRepositoryImpl(
    private val parser: Parser<MovieModel>
) : MovieRepository {

    override fun getMovieDetail(): Single<MovieModel> {
        return wrapperGetMovieDetailRx()
    }

    private fun wrapperGetMovieDetailRx(): Single<MovieModel> {
        return Single.fromCallable {
            parser.parse(SAMPLE_DATA)
            parser.getResult()
        }
    }

    companion object {
        const val SAMPLE_DATA = "{'title':'Civil War','image':['http://movie.phinf.naver.net/20151127_272/1448585271749MCMVs_JPEG/movie_image.jpg?type=m665_443_2','http://movie.phinf.naver.net/20151127_84/1448585272016tiBsF_JPEG/movie_image.jpg?type=m665_443_2','http://movie.phinf.naver.net/20151125_36/1448434523214fPmj0_JPEG/movie_image.jpg?type=m665_443_2']}"
    }
}