package com.arts.mapapeli.application

import com.arts.mapapeli.data.remoteApi.WebServicesMap
import com.arts.mapapeli.data.remoteApi.WebServicesMovieDB
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    var gson = GsonBuilder()
        .setLenient()
        .create()

    val webServicesMap by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_MAPS)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WebServicesMap::class.java)
    }

    val webServicesMovie by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_THE_MOVIE_DB)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WebServicesMovieDB::class.java)
    }
}