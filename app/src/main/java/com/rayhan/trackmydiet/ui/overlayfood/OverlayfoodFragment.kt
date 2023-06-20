package com.rayhan.trackmydiet.ui.overlayfood

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rayhan.trackmydiet.R
import com.rayhan.trackmydiet.adapter.FoodResultAdapter
import com.rayhan.trackmydiet.databinding.FragmentOverlayfoodBinding
import com.rayhan.trackmydiet.model.DailyMealPost
import com.rayhan.trackmydiet.model.FoodSearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

private const val TAG = "OverlayfoodFragment"

class OverlayfoodFragment : DialogFragment() {

    private var _binding: FragmentOverlayfoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OverlayFoodViewModel
    private lateinit var foodResultAdapter: FoodResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = FragmentOverlayfoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(OverlayFoodViewModel::class.java)

        foodResultAdapter = FoodResultAdapter(::insertItem)
        binding.rvResults.adapter = foodResultAdapter
        binding.rvResults.layoutManager = LinearLayoutManager(context)

        binding.clFoodResult.isVisible = false

        binding.btnPost.setOnClickListener {
            val search = binding.etName.text.toString()
            val size = binding.etServingSize.text.toString()

            if (size.isEmpty() && search.isEmpty()) {
                return@setOnClickListener
            } else {
                viewModel.retrieveEdamamFoodInformation(search, size.toInt())
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.foodResult.observe(viewLifecycleOwner, { foodResult ->
            if (foodResult.isNotEmpty()) {
                foodResultAdapter.setData(foodResult)
                binding.clFoodResult.isVisible = true
            } else {
                binding.clFoodResult.isVisible = false
            }
        })
    }

    private fun insertItem(dailyMeal: DailyMealPost) {
        val emailName = getEmailName()
        val user = com.rayhan.trackmydiet.model.User("", emailName)
        dailyMeal.user = user
        viewModel.addMealDB(dailyMeal)
        dismiss()
    }

    private fun getEmailName(): String {
        val userName = Firebase.auth.currentUser
        var currentUserName = ""
        userName?.let {
            for (profile in it.providerData) {
                currentUserName = profile.email.toString()
            }
        }
        return currentUserName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

