package com.rayhan.trackmydiet.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rayhan.trackmydiet.databinding.ActivityReceipeDetailBinding
import com.rayhan.trackmydiet.model.Hit
import kotlin.math.roundToInt

class ReceipeDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityReceipeDetailBinding

    private lateinit var recipeInfo : Hit

    private lateinit var ivRecipe: ImageView
    private lateinit var tvRecipeName: TextView

    private lateinit var tvTimeAmt: TextView
    private lateinit var tvCalorieAmt: TextView

    private lateinit var tvRecipeProtien: TextView
    private lateinit var tvRecipeCarbs: TextView
    private lateinit var tvRecipeFat : TextView

    private lateinit var tvIngredient: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        initView()
        recipeInfo = intent.getSerializableExtra("recipeInfo") as Hit

        binding.btnBack.setOnClickListener {
            finish()
        }

        assignValue()
    }

    @SuppressLint("SetTextI18n")
    private fun assignValue() {
        tvRecipeName.text = recipeInfo.recipe.label
        var calories = recipeInfo.recipe.calories.roundToInt()
        val serving  = recipeInfo.recipe.yield

        calories = calories.div(serving).roundToInt()

        tvTimeAmt.text = "${recipeInfo.recipe.totalTime.roundToInt()} minutes"
        tvCalorieAmt.text = "$calories kcal"

        tvRecipeProtien.text = "${recipeInfo.recipe.totalNutrients.PROCNT.quantity.roundToInt()} g"
        tvRecipeCarbs.text = "${recipeInfo.recipe.totalNutrients.cho.quantity.roundToInt()} g"
        tvRecipeFat.text = "${recipeInfo.recipe.totalNutrients.FAT.quantity.roundToInt()} g"


        var ingredientText = ""
        for(item in recipeInfo.recipe.ingredientLines){
            ingredientText += item +"\n"
        }
        tvIngredient.text = ingredientText
        val image_url = recipeInfo.recipe.image
        Glide.with(this)
            .load(image_url)
            .into(ivRecipe)
    }

    private fun initView() {
        tvRecipeName = binding.tvFoodName
        ivRecipe = binding.ivFoodImage
        tvTimeAmt = binding.tvTime
        tvCalorieAmt = binding.tvCalories
        tvIngredient = binding.tvIngredients
        tvRecipeProtien = binding.tvProteinAmnt
        tvRecipeCarbs = binding.tvCarbsAmnt
        tvRecipeFat = binding.tvFatAmnt
    }
}