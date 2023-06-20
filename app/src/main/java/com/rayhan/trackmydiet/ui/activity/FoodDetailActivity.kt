package com.rayhan.trackmydiet.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.rayhan.trackmydiet.MainActivity
import com.rayhan.trackmydiet.R
import com.rayhan.trackmydiet.databinding.ActivityFoodDetailBinding
import com.rayhan.trackmydiet.model.DailyMealPost
import com.squareup.picasso.Picasso

class FoodDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodDetailBinding

    private lateinit var mealInfo : DailyMealPost
    private lateinit var etServingSize : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        mealInfo  = intent.getSerializableExtra("mealItem") as DailyMealPost

        binding.tvFoodName.text = mealInfo.name
        binding.tvCalorieAmnt.text = mealInfo.calories.toString()
        binding.tvCarbsAmnt.text = mealInfo.carbs.toString()
        binding.tvProteinAmnt.text = mealInfo.protein.toString()
        binding.tvFatAmnt.text = mealInfo.fat.toString()
        etServingSize = binding.etServingSize
        etServingSize.setText(mealInfo.serving.toString())

        Picasso.get()
            .load(mealInfo.image)
            .placeholder(R.drawable.bacground_myfood)
            .error(R.drawable.bacground_myfood)
            .into(binding.ivFoodImage)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnUpdate.setOnClickListener {
            val newServingSize = etServingSize.text.toString().toInt()
            updateServingSize(newServingSize)
        }
        binding.btnRemove.setOnClickListener {
            removeMealFromFirestore()
        }
    }

    private fun removeMealFromFirestore() {
        val firestoreDb = FirebaseFirestore.getInstance()
        val mealReference = mealInfo.id?.let { firestoreDb.collection("userDailyMeal").document(it) }

        if (mealReference != null) {
            mealReference.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Meal removed successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to remove meal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateServingSize(newServingSize: Int) {
        val firestoreDb = FirebaseFirestore.getInstance()
        val mealReference = mealInfo.id?.let { firestoreDb.collection("userDailyMeal").document(it) }

        if (mealReference != null) {
            // Calculate new values based on the new serving size
            val updatedCarbs = (mealInfo.carbs / mealInfo.serving) * newServingSize
            val updatedProtein = (mealInfo.protein / mealInfo.serving) * newServingSize
            val updatedFat = (mealInfo.fat / mealInfo.serving) * newServingSize
            val updatedCalories = (mealInfo.calories / mealInfo.serving) * newServingSize

            // Update the Firestore document with the new values
            val updateData = hashMapOf(
                "serving" to newServingSize,
                "carbs" to updatedCarbs,
                "protein" to updatedProtein,
                "fat" to updatedFat,
                "calories" to updatedCalories
            )

            mealReference.update(updateData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Serving size and nutrient values updated successfully", Toast.LENGTH_SHORT).show()

                    mealInfo.serving = newServingSize

                    binding.tvCalorieAmnt.text = updatedCalories.toString()
                    binding.tvCarbsAmnt.text = updatedCarbs.toString()
                    binding.tvProteinAmnt.text = updatedProtein.toString()
                    binding.tvFatAmnt.text = updatedFat.toString()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update serving size and nutrient values: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}