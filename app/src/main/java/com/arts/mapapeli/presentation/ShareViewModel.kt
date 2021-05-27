package com.arts.mapapeli.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arts.mapapeli.data.model.MapDriverResponse

class ShareViewModel: ViewModel() {
    private val _dataShare: MutableLiveData<MapDriverResponse> = MutableLiveData<MapDriverResponse>()
    val dataShareLiveData: LiveData<MapDriverResponse>
        get() = getData()

    fun updateData(data: MapDriverResponse){
        Log.i("viewMoldelShare", "valor set= $data")
        _dataShare.value = data
    }

    fun getData(): MutableLiveData<MapDriverResponse> {
        Log.i("viewMoldelShare", "valor get= ${_dataShare.value}")
        return _dataShare
    }

}