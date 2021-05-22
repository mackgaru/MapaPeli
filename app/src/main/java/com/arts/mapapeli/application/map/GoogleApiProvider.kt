package com.arts.mapapeli.application.map

import android.content.Context
import com.arts.mapapeli.R
import com.arts.mapapeli.application.MyApp
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call

class GoogleApiProvider {

    /*
    private var context: Context? = null
    private var retrofitClientMaps: RetrofitClientMaps = RetrofitClientMaps()

    fun GoogleApiProvider(context: Context?) {
        this.context = context
    }

    fun getDirections(originLatLgn: LatLng, destinoLatLgn: LatLng): Call<String> {
        val baseUrl = "https://maps.googleapis.com"
        val query = ("/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                    + "origin=" + originLatLgn.latitude + "," + originLatLgn.longitude + "&"
                    + "destination=" + destinoLatLgn.latitude + "," + destinoLatLgn.longitude + "&"
                    + "key=" + MyApp.context.resources.getString(R.string.google_maps_key))
        return retrofitClientMaps.clienteMaps(baseUrl)!!.create(IGoogleApi::class.java).getDirections(baseUrl + query)
    }

     */
}