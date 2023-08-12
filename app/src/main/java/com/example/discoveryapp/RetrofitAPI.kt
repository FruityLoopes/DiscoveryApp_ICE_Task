package com.example.discoveryapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
interface RetrofitAPI {
    @POST("userInput")
    // on below line we are creating a method to post our data.
    fun postData(@Body userInput:UserInput ?): Call<UserInput?>?
}