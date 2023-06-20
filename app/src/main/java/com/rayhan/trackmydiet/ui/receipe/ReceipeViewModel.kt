package com.rayhan.trackmydiet.ui.receipe

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rayhan.trackmydiet.data.remote.FoodReceipeConfig.APP_ID_FOOD_DB
import com.rayhan.trackmydiet.data.remote.FoodReceipeConfig.APP_KEY_FOOD_DB
import com.rayhan.trackmydiet.data.remote.FoodReceipeConfig.BASE_URL
import com.rayhan.trackmydiet.data.remote.RecipeService
import com.rayhan.trackmydiet.model.Hit
import com.rayhan.trackmydiet.model.RecipeResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReceipeViewModel : ViewModel() {
    private val _recipeResult = MutableLiveData<List<Hit>>()
    val recipeResult: LiveData<List<Hit>> = _recipeResult

    fun retrieveEdamamFoodInformation(foodSearch: String) {
        val type = "public"
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val edamamRecipeService = retrofit.create(RecipeService::class.java)
        edamamRecipeService.recipeInfo(
            type,
            foodSearch,
            APP_ID_FOOD_DB,
            APP_KEY_FOOD_DB
        ).enqueue(object : Callback<RecipeResult> {
            override fun onResponse(call: Call<RecipeResult>, response: Response<RecipeResult>) {
                val body = response.body()
                if (body == null) {
                    _recipeResult.value = emptyList()
                    Log.i(TAG, "Did not receive valid response from Edamam Food Service")
                    return
                }
                _recipeResult.value = body.hits
            }

            override fun onFailure(call: Call<RecipeResult>, t: Throwable) {
                Log.i(TAG, "Failed to get info $t")
            }
        })
    }
}
