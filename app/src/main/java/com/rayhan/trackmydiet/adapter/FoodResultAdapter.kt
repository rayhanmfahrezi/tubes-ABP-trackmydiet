package com.rayhan.trackmydiet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayhan.trackmydiet.R
import com.rayhan.trackmydiet.databinding.ItemFoodResultBinding
import com.rayhan.trackmydiet.model.DailyMealPost
import com.squareup.picasso.Picasso

class FoodResultAdapter(
    private val insertDB: (DailyMealPost) -> Unit
) : RecyclerView.Adapter<FoodResultAdapter.FoodResultViewHolder>() {

    private val foodResult = mutableListOf<DailyMealPost>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodResultViewHolder {
        val binding = ItemFoodResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodResultViewHolder, position: Int) {
        val foodItem = foodResult[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int {
        return foodResult.size
    }

    fun setData(data: List<DailyMealPost>) {
        foodResult.clear()
        foodResult.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData() {
        foodResult.clear()
        notifyDataSetChanged()
    }

    inner class FoodResultViewHolder(private val binding: ItemFoodResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: DailyMealPost) {
            binding.tvFoodName.text = foodItem.name

            var image = foodItem.image
            if (image.isEmpty()) {
                image =
                    "https://firebasestorage.googleapis.com/v0/b/textdemo-9e9b1.appspot.com/o/posts%2FFri%20Sep%2010%2015%3A52%3A09%20PDT%202021.png?alt=media&token=a774304d-da5b-4cb9-8fbc-853f8ff6a78f"
            }

            Picasso.get()
                .load(image)
                .placeholder(R.drawable.bacground_myfood)
                .into(binding.ivFoodImage)

            binding.clFoodResult.setOnClickListener {
                insertDB(foodItem)
            }
        }
    }
}
