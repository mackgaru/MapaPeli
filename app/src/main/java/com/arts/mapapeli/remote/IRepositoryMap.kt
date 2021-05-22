package com.arts.mapapeli.domain.remote

import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.data.model.MapDriverResponse
import com.arts.mapapeli.data.model.MovieNowPlaying

interface IRepositoryMap {

    //directionDriverMap
    suspend fun directionDriverMap(
        mode: String,
        transit_routing_preferences: String,
        origin: String,
        destination: String,
        key: String
    ): Resource.Success<MapDriverResponse>

}