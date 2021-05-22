package com.arts.mapapeli.domain.remote

import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.data.model.MapDriverResponse
import com.arts.mapapeli.data.model.MovieNowPlaying
import com.arts.mapapeli.data.source.DataSource

class RepositoryMapImpl(private val dataSource: DataSource): IRepositoryMap {
    override suspend fun directionDriverMap(
        mode: String,
        transit_routing_preferences: String,
        origin: String,
        destination: String,
        key: String
    ): Resource.Success<MapDriverResponse> {
        return dataSource.directionDriverMap(mode, transit_routing_preferences, origin, destination, key)
    }
}