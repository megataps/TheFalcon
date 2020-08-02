package com.megalabs.falcon.naming

interface FileNamGenerator {
    fun generate(url: String): String
}