package com.udacity.asteroidradar.api

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
data class PictureOfDay(
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    @Json(name = "url") val url: String
): Parcelable

