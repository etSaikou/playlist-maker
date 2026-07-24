package com.saikou.playlistmaker.util

sealed class Resource<T>(val data: T? = null, val message: String? = null, val additionalMessage: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, additionalMessage: String?,data: T? = null): Resource<T>(data, message, additionalMessage)
}