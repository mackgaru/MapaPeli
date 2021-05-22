package com.arts.mapapeli.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val puntuacion: Double,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("overview") val descripcion: String,
):Parcelable{
    val imagenUrl: String = ""
    val generos: List<String> = mutableListOf()
}
