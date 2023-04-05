package com.example.alloyweather.logic.network

import com.example.alloyweather.AlloyWeatherApplication
import com.example.alloyweather.logic.model.DaliyResponse
import com.example.alloyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${AlloyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String,@Path("lat") lat: String): Call<RealtimeResponse>

    @GET("v2.5/${AlloyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String,@Path("lat") lat: String): Call<DaliyResponse>
}