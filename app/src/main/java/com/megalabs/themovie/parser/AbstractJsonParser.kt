package com.megalabs.themovie.parser

import com.megalabs.falcon.IOUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


abstract class AbstractJsonParser<T> : Parser<T> {

    private var mResult: T? = null

    @Throws(JSONException::class)
    override fun parse(data: ByteArray) {
        val jObj = JSONObject(
            IOUtils.bytesToString(
                data
            )
        )
        mResult = parse(jObj)
    }

    @Throws(JSONException::class)
    override fun parse(data: String) {
        val jObj = JSONObject(data)
        mResult = parse(jObj)
    }

    override fun getResult(): T {
        return mResult as T
    }

    @Throws(JSONException::class)
    protected abstract fun parse(jsonParser: JSONObject?): T

    @Throws(JSONException::class)
    protected fun getObject(tagName: String?, jObj: JSONObject): JSONObject {
        return jObj.getJSONObject(tagName)
    }

    @Throws(JSONException::class)
    protected fun getArrayObject(tagName: String?, jObj: JSONObject): JSONArray {
        return jObj.getJSONArray(tagName)
    }

    @Throws(JSONException::class)
    protected fun getString(tagName: String?, jObj: JSONObject): String {
        return if (jObj.has(tagName)) {
            jObj.getString(tagName)
        } else ""
    }

    @Throws(JSONException::class)
    protected fun getDouble(tagName: String?, jObj: JSONObject): Float {
        return if (jObj.has(tagName)) {
            jObj.getDouble(tagName).toFloat()
        } else 0f
    }

    @Throws(JSONException::class)
    protected fun getInt(tagName: String?, jObj: JSONObject): Int {
        return if (jObj.has(tagName)) {
            jObj.getInt(tagName)
        } else 0
    }

    @Throws(JSONException::class)
    protected fun getBoolean(tagName: String?, jObj: JSONObject): Boolean {
        return if (jObj.has(tagName)) {
            jObj.getBoolean(tagName)
        } else false
    }
}