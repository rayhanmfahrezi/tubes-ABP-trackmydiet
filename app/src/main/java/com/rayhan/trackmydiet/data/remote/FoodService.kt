package com.rayhan.trackmydiet.data.remote

import com.rayhan.trackmydiet.model.FoodSearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodService {
    @GET("parser")
    fun foodInfo(
        @Query("app_id") app_id:String,
        @Query("app_key") app_key :String,
        @Query("ingr") ingr :String,
        @Query("nutrition-type") nutrition_type :String,
    ): Call<FoodSearchResult>
}