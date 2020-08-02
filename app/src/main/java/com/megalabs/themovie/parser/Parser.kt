package com.megalabs.themovie.parser

import org.json.JSONException


interface Parser<T> {

    fun getResult(): T

    @Throws(JSONException::class)
    fun parse(data: ByteArray)

    @Throws(JSONException::class)
    fun parse(data: String)


}