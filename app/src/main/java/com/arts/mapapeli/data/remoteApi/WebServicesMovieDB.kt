package com.arts.mapapeli.data.remoteApi

import com.arts.mapapeli.application.Constants
import com.arts.mapapeli.data.model.MovieNowPlaying
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WebServicesMovieDB {

    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET(Constants.MOVIE_NOW_PLAYING)
    suspend fun movieNowPlaying(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("page") page: String
    ):MovieNowPlaying

    @Headers("Content-Type: application/json; charset=UTF-8")
    @GET(Constants.MOVIE_GENERO)
    suspend fun movieGenero(
        @Query("api_key") api_key: String,
        @Query("language") language: String
    ):MovieNowPlaying

}