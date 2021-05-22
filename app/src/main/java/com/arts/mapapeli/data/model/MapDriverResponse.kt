package com.arts.mapapeli.data.model
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapDriverResponse(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint>,
    @SerializedName("routes")
    val routes: List<Route>,
    @SerializedName("status")
    val status: String
):Parcelable

@Parcelize
data class GeocodedWaypoint(
    @SerializedName("geocoder_status")
    val geocoderStatus: String,
    @SerializedName("place_id")
    val placeId: String,
    @SerializedName("types")
    val types: List<String>
):Parcelable

@Parcelize
data class Route(
    @SerializedName("bounds")
    val bounds: Bounds,
    @SerializedName("copyrights")
    val copyrights: String,
    @SerializedName("legs")
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline,
    @SerializedName("summary")
    val summary: String
):Parcelable

@Parcelize
data class Bounds(
    @SerializedName("northeast")
    val northeast: Northeast,
    @SerializedName("southwest")
    val southwest: Southwest
):Parcelable

@Parcelize
data class Leg(
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("end_address")
    val endAddress: String,
    @SerializedName("end_location")
    val endLocation: EndLocation,
    @SerializedName("start_address")
    val startAddress: String,
    @SerializedName("start_location")
    val startLocation: StartLocation,
    @SerializedName("steps")
    val steps: List<Step>
):Parcelable

@Parcelize
data class OverviewPolyline(
    @SerializedName("points")
    val points: String
):Parcelable

@Parcelize
data class Northeast(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
):Parcelable

@Parcelize
data class Southwest(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
):Parcelable

@Parcelize
data class Distance(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
):Parcelable

@Parcelize
data class Duration(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
):Parcelable

@Parcelize
data class EndLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
):Parcelable

@Parcelize
data class StartLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
):Parcelable

@Parcelize
data class Step(
    @SerializedName("distance")
    val distance: DistanceX,
    @SerializedName("duration")
    val duration: DurationX,
    @SerializedName("end_location")
    val endLocation: EndLocationX,
    @SerializedName("html_instructions")
    val htmlInstructions: String,
    @SerializedName("maneuver")
    val maneuver: String,
    @SerializedName("polyline")
    val polyline: Polyline,
    @SerializedName("start_location")
    val startLocation: StartLocationX,
    @SerializedName("travel_mode")
    val travelMode: String
):Parcelable

@Parcelize
data class DistanceX(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
):Parcelable

@Parcelize
data class DurationX(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
):Parcelable

@Parcelize
data class EndLocationX(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
):Parcelable

@Parcelize
data class Polyline(
    @SerializedName("points")
    val points: String
):Parcelable

@Parcelize
data class StartLocationX(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
):Parcelable