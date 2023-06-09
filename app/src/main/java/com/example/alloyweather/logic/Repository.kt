package com.example.alloyweather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.example.alloyweather.logic.dao.PlaceDao
import com.example.alloyweather.logic.model.Place
import com.example.alloyweather.logic.model.Weather
import com.example.alloyweather.logic.network.AlloyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.Dispatcher
import kotlin.coroutines.CoroutineContext

object Repository {
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = AlloyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                AlloyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                AlloyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val daliyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && daliyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, daliyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" + "daily response status is ${daliyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext,block: suspend () -> Result<T>) = liveData<Result<T>>(context) {
        val result = try {
            block()
        } catch (e :Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}