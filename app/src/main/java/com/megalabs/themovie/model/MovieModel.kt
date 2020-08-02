package com.megalabs.themovie.model

data class MovieModel(
    val title: String,
    val images: List<ImageModel> = emptyList()
) {
}