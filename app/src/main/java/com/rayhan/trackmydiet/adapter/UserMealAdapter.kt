package com.rayhan.trackmydiet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayhan.trackmydiet.R
import com.rayhan.trackmydiet.databinding.ItemUserFoodBinding
import com.rayhan.trackmydiet.model.DailyMealPost
import com.rayhan.trackmydiet.ui.activity.FoodDetailActivity
import com.squareup.picasso.Picasso

class UserMealAdapter(private val userList:ArrayList<DailyMealPost>): RecyclerView.Adapter<UserMealAdapter.UserMealAdapterHolder>() {

    private var _binding: ItemUserFoodBinding? = null
    private val binding get() = _binding!!

    class UserMealAdapterHolder(itemView: ItemUserFoodBinding) : RecyclerView.ViewHolder(itemView.root){
        fun bind(currentItem: DailyMealPost, binding: ItemUserFoodBinding) {
            binding.tvFoodName.text = currentItem.name
            binding.tvCalorie.text = "${currentItem.calories} calories"
            binding.tvServing.text = "${currentItem.serving} serving, "

            if(currentItem.image.isEmpty()){
                Picasso.get()
                    .load(R.drawable.bacground_myfood)
                    .placeholder(R.drawable.bacground_myfood)
                    .resize(150, 150)
                    .centerCrop()
                    .into(binding.ivUserFoodImage)
            }else{
                Picasso.get()
                    .load(currentItem.image)
                    .placeholder(R.drawable.bacground_myfood)
                    .error(R.drawable.bacground_myfood)
                    .resize(150, 150)
                    .centerCrop()
                    .into(binding.ivUserFoodImage)
            }

            binding.rvUserFurtherDetail.setOnClickListener {
                val intent  = Intent(itemView.context, FoodDetailActivity::class.java)
                intent.putExtra("mealItem", currentItem)
                itemView.context.startActivity(intent)
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserMealAdapterHolder {
        _binding = ItemUserFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserMealAdapterHolder(_binding!!)
    }

    override fun onBindViewHolder(holder: UserMealAdapterHolder, position: Int) {
        val currentItem = userList[position]
        holder.bind(currentItem, binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}