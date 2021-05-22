package com.arts.mapapeli.domain.remote

import com.arts.mapapeli.application.Constants
import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.data.model.MovieNowPlaying
import com.arts.mapapeli.data.source.DataSource

class RepositoryMovieImpl(private val dataSource: DataSource): IRepositoryMovie {

    override suspend fun movieNowPlaying(): Resource.Success<MovieNowPlaying> {
        return dataSource.movieNowPlaying(Constants.API_THE_MOVIE_DB, "es-ES", "1")
    }

}