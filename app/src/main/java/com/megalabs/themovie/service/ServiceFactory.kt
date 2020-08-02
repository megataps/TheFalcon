package com.megalabs.themovie.service

interface ServiceFactory {

    fun getMovieService(): MovieService
}