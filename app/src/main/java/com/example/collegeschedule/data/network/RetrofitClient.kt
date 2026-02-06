package com.example.collegeschedule.data.network

import com.example.collegeschedule.data.api.ScheduleApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5164/"

    val api: ScheduleApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScheduleApi::class.java)
    }
}