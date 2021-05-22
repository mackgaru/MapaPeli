package com.arts.mapapeli.application

sealed class Resource<out T> {
    class Loading<out T>: Resource<T>()
    data class Success<out T>(val data: T): Resource<T>()
    data class Failure(val exception: Exception): Resource<Nothing>()
    data class UnknowError(val error: Throwable): Resource<Nothing>()
}