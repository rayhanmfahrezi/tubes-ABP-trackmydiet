package com.rayhan.trackmydiet.data.remote

import com.rayhan.trackmydiet.model.RecipeResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeService{
    @GET("v2?")
    fun recipeInfo(
        @Query("type") type:String,
        @Query("q") foodEntry :String,
        @Query("app_id") app_id :String,
        @Query("app_key") app_key :String,
    ): Call<RecipeResult>
}