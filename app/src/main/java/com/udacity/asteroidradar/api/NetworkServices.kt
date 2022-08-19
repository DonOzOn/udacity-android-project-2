package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkServices {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroid(@Query("start_date") start_date: String, @Query("end_date") end_date: String):String

    @GET("planetary/apod")
    fun getImgOfTheDay():Deferred<PictureOfDay>
}
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URL)
    .client(
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val url = chain
                    .request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", Constants.API_KEY)
                    .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
            }
            .build()
    )
    .build()

object AsteroidApi {
    val retrofitService : NetworkServices by lazy { retrofit.create(NetworkServices::class.java) }
}
