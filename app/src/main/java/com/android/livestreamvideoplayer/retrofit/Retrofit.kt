package com.android.livestreamvideoplayer.retrofit

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Retrofit {

    fun getRetrofit(baseUrl: String) : Retrofit {
       return Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create()).baseUrl(baseUrl).build()
    }
}