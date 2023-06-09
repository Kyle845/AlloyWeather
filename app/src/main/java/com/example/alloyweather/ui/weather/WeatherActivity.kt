package com.example.alloyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.alloyweather.R
import com.example.alloyweather.databinding.ActivityWeatherBinding
import com.example.alloyweather.logic.model.Weather
import com.example.alloyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    private lateinit var binding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.statusBarColor = Color.TRANSPARENT
        } else {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this,"无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        val navBtn = findViewById<Button>(R.id.navBtn)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

    }
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.isRefreshing = true
    }
    private fun showWeatherInfo(weather: Weather) {
            val placeName = findViewById<TextView>(R.id.placeName)
            placeName.text = viewModel.placeName
            val realtime = weather.realtime
            val daily = weather.daily
            val currentTempText = "${realtime.temperature.toInt()} °C"
            val currentTemp = findViewById<TextView>(R.id.currentTemp)
            currentTemp.text = currentTempText
            val currentSky = findViewById<TextView>(R.id.currentSky)
            currentSky.text = getSky(realtime.skycon).info
            val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
            val currentAQI = findViewById<TextView>(R.id.currentAQI)
            currentAQI.text = currentPM25Text
            val nowLayout = findViewById<View>(R.id.nowLayout)
            nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
            val forecastLayout = findViewById<LinearLayout>(R.id.forecastLayout)
            forecastLayout.removeAllViews()
            val days = daily.skycon.size
            for (i in 0 until days) {
                val skycon = daily.skycon[i]
                val temperature = daily.temperature[i]
                val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)
                val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
                val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
                val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
                val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateInfo.text = simpleDateFormat.format(skycon.date)
                val sky = getSky(skycon.value)
                skyIcon.setImageResource(sky.icon)
                skyInfo.text = sky.info
                val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} °C"
                temperatureInfo.text = tempText
                forecastLayout.addView(view)
            }
            val lifeIndex = daily.lifeIndex
            val coldRiskText = findViewById<TextView>(R.id.coldRiskText)
            coldRiskText.text = lifeIndex.coldRisk[0].desc
            val dressingText = findViewById<TextView>(R.id.dressingText)
            dressingText.text = lifeIndex.dressing[0].desc
            val ultravioletText = findViewById<TextView>(R.id.ultravioletText)
            ultravioletText.text = lifeIndex.ultraviolet[0].desc
            val carWashingText = findViewById<TextView>(R.id.carWashingText)
            carWashingText.text = lifeIndex.carWashing[0].desc
            val weatherLayout = findViewById<ScrollView>(R.id.weatherLayout)
            weatherLayout.visibility = View.VISIBLE

    }
}