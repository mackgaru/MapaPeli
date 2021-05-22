package com.arts.mapapeli.domain.remote

import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.data.model.MovieNowPlaying

interface IRepositoryMovie {
    //movieNowPlaying
    suspend fun movieNowPlaying(): Resource.Success<MovieNowPlaying>
}