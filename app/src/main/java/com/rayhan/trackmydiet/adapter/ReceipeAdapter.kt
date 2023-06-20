package com.rayhan.trackmydiet.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayhan.trackmydiet.R
import com.rayhan.trackmydiet.databinding.ItemReceipeResultBinding
import com.rayhan.trackmydiet.model.Hit
import com.rayhan.trackmydiet.ui.activity.ReceipeDetailActivity
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class RecipeAdapter(private var recipe: ArrayList<Hit>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemReceipeResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipeItem = recipe[position]
        holder.bind(recipeItem)
    }

    override fun getItemCount(): Int {
        return recipe.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setData(newData: List<Hit>) {
        recipe.clear()
        recipe.addAll(newData)
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(private val binding: ItemReceipeResultBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(hit: Hit) {
            var calories = hit.recipe.calories.roundToInt()
            val serving = hit.recipe.yield
            calories = calories.div(serving).toInt()

            binding.tvFoodName.text = hit.recipe.label
            binding.tvCalories.text = "$calories calories"

            val time = "${hit.recipe.totalTime.roundToInt()} mins"
            binding.tvCategory.text = time

            val imageUrl = hit.recipe.image
            val placeholder = R.drawable.bacground_myfood

            Picasso.get()
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .resize(150, 150)
                .centerCrop()
                .into(binding.ivFood)

            binding.rvReceipeFurtherDetail.setOnClickListener {
                val intent = Intent(itemView.context, ReceipeDetailActivity::class.java)
                intent.putExtra("recipeInfo", hit)
                itemView.context.startActivity(intent)
            }
        }
    }
}
