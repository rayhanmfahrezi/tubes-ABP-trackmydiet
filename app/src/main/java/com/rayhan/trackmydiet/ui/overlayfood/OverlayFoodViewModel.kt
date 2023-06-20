package com.rayhan.trackmydiet.ui.overlayfood

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.rayhan.trackmydiet.data.remote.FoodApiConfig
import com.rayhan.trackmydiet.data.remote.FoodService
import com.rayhan.trackmydiet.model.DailyMealPost
import com.rayhan.trackmydiet.model.FoodSearchResult
import com.rayhan.trackmydiet.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class OverlayFoodViewModel : ViewModel() {

    private val _foodResult = MutableLiveData<List<DailyMealPost>>()
    val foodResult: LiveData<List<DailyMealPost>> = _foodResult

    private val foodService: FoodService = createFoodService()

    fun retrieveEdamamFoodInformation(ingr: String, servingSize: Int) {
        foodService.foodInfo(FoodApiConfig.APP_ID_FOOD_DB, FoodApiConfig.APP_KEY_FOOD_DB, ingr, "cooking")
            .enqueue(object : Callback<FoodSearchResult> {
                override fun onResponse(
                    call: Call<FoodSearchResult>,
                    response: Response<FoodSearchResult>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            val foodResult = mutableListOf<DailyMealPost>()

                            for (item in body.hints) {
                                val name = item.food.label
                                val protein = item.food.nutrients.PROCNT.toLong().times(servingSize)
                                val calories = item.food.nutrients.ENERC_KCAL.toLong().times(servingSize)
                                val fat = item.food.nutrients.FAT.toLong().times(servingSize)
                                val carbs = item.food.nutrients.CHOCDF.toLong().times(servingSize)
                                var image = item.food.image
                                val time = System.currentTimeMillis()
                                val serving = servingSize

                                if (image.isNullOrEmpty()) {
                                    image =
                                        "https://firebasestorage.googleapis.com/v0/b/textdemo-9e9b1.appspot.com/o/posts%2FFri%20Sep%2010%2015%3A52%3A09%20PDT%202021.png?alt=media&token=a774304d-da5b-4cb9-8fbc-853f8ff6a78f"
                                }

                                val user = User("", "")
                                val meal = DailyMealPost(
                                    "",
                                    name,
                                    protein,
                                    calories,
                                    fat,
                                    carbs,
                                    image,
                                    serving,
                                    time,
                                    Date(),
                                    user
                                )

                                foodResult.add(meal)
                            }

                            _foodResult.value = foodResult
                        }
                    } else {
                        Log.w(TAG, "Did not receive valid response body from Food API")
                    }
                }

                override fun onFailure(call: Call<FoodSearchResult>, t: Throwable) {
                    Log.e(TAG, "onFailure", t)
                }
            })
    }

    private fun createFoodService(): FoodService {
        val retrofit = Retrofit.Builder()
            .baseUrl(FoodApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(FoodService::class.java)
    }

    fun addMealDB(mealPost: DailyMealPost) {
        val addMeal = FirebaseFirestore.getInstance().collection("userDailyMeal")
        addMeal.add(mealPost)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Meal added successfully
                } else {
                    // Error adding meal
                }
            }
    }
}


