package com.arts.mapapeli.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.domain.remote.IRepositoryMap
import kotlinx.coroutines.Dispatchers

class MapaViewModel(private val repositoryMap: IRepositoryMap) : ViewModel() {

    fun directionDriverMap(
        mode: String,
        transit_routing_preferences: String,
        origin: String,
        destination: String,
        key: String
    ) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repositoryMap.directionDriverMap(mode, transit_routing_preferences, origin, destination, key)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

}

class MapaViewModelFactory(private val repositoryMap: IRepositoryMap): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IRepositoryMap::class.java).newInstance(repositoryMap)
    }
}