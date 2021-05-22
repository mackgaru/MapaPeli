package com.arts.mapapeli.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genero(
    @SerializedName("genres")
    val genres: List<Genre>
):Parcelable

@Parcelize
data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
):Parcelable