package com.arts.mapapeli.data.remoteApi

import com.arts.mapapeli.application.Constants
import com.arts.mapapeli.data.model.MapDriverResponse
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface WebServicesMap {

    //directionDriverMap
    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST(Constants.DIRECTION_DRIVER)
    suspend fun directionDriverMap(
        @Query("mode") mode: String,
        @Query("transit_routing_preferences") transit_routing_preferences:String,
        @Query("origin") origin:String,
        @Query("destination") destination:String,
        @Query("key") key: String
    ): MapDriverResponse

}