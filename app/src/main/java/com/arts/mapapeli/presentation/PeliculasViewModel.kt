package com.arts.mapapeli.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.domain.remote.IRepositoryMovie
import kotlinx.coroutines.Dispatchers

class PeliculasViewModel(private val repositoryMovie: IRepositoryMovie) : ViewModel() {

    fun movieNowPlaying() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repositoryMovie.movieNowPlaying()))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

}

class PeliculasViewModelFactory(private val repositoryMap: IRepositoryMovie): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IRepositoryMovie::class.java).newInstance(repositoryMap)
    }
}