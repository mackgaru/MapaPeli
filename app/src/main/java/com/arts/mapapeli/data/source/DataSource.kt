package com.arts.mapapeli.data.source

import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.application.RetrofitService
import com.arts.mapapeli.data.model.MapDriverResponse
import com.arts.mapapeli.data.model.MovieNowPlaying

class DataSource {

    //directionDriverMap
    suspend fun directionDriverMap(
        mode: String,
        transit_routing_preferences: String,
        origin: String,
        destination: String,
        key: String
    ): Resource.Success<MapDriverResponse>{
        return Resource.Success(RetrofitService.webServicesMap.directionDriverMap(mode, transit_routing_preferences, origin, destination, key))
    }

    //movieNowPlaying
    suspend fun movieNowPlaying(
        api_key: String,
        language: String,
        page: String
    ): Resource.Success<MovieNowPlaying>{
        return Resource.Success(RetrofitService.webServicesMovie.movieNowPlaying(api_key, language, page))
    }

}