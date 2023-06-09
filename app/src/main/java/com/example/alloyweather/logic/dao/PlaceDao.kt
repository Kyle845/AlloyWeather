package com.example.alloyweather.logic.dao

import android.content.Context
import android.provider.Settings.System.putString
import androidx.core.content.edit
import com.example.alloyweather.AlloyWeatherApplication
import com.example.alloyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    private fun sharedPreferences() = AlloyWeatherApplication.context.getSharedPreferences("alloy_weather",Context.MODE_PRIVATE)
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")
}