package com.megalabs.themovie.parser

import com.megalabs.themovie.model.ImageModel
import com.megalabs.themovie.model.MovieModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MovieParser : AbstractJsonParser<MovieModel>() {

    override fun parse(jsonParser: JSONObject?): MovieModel {
        jsonParser?.let {
            val title = getString("title", it)
            val images = getImages(it.getJSONArray("image"))
            return MovieModel(title, images)
        }

        throw NullPointerException("Movie json is null")
    }

    @Throws(JSONException::class)
    private fun getImages(imageListJson: JSONArray): List<ImageModel> {
        val images: MutableList<ImageModel> = ArrayList()
        var image: ImageModel
        val length = imageListJson.length()
        for (i in 0 until length) {
            image = ImageModel(imageListJson[i] as String)
            images.add(image)
        }
        return images
    }


}