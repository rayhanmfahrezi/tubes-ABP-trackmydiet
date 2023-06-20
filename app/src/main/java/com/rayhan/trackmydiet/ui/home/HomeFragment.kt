package com.rayhan.trackmydiet.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.rayhan.trackmydiet.adapter.UserMealAdapter
import com.rayhan.trackmydiet.databinding.FragmentHomeBinding
import com.rayhan.trackmydiet.model.DailyMealPost
import com.rayhan.trackmydiet.model.UserData
import com.rayhan.trackmydiet.ui.overlayfood.OverlayfoodFragment
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var mealListener: ListenerRegistration
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var userRecylerView: RecyclerView
    private lateinit var userMealArrayList: ArrayList<DailyMealPost>

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        firestoreDb = FirebaseFirestore.getInstance()

        binding.btnAddFood.setOnClickListener {
            openFoodItem()
        }

        userRecylerView = binding.rvDailyFood
        userRecylerView.layoutManager = LinearLayoutManager(context)

        userMealArrayList = arrayListOf()

        binding.tvHeloUser.text = "Hello, ${getUsername()}"
        progressBar = binding.progressBar

        setBaseGoalText { calories ->
            binding.tvBaseGoal.text = calories.toString() }

        binding.btnSelectDate.setOnClickListener {
            Toast.makeText(requireContext(), "This feature is under development", Toast.LENGTH_SHORT).show()
        }

        binding.btnFav.setOnClickListener {
            Toast.makeText(requireContext(), "This feature is under development", Toast.LENGTH_SHORT).show()
        }

        fetchData()
    }

    private fun openFoodItem() {
        val newFragment = OverlayfoodFragment()
        newFragment.show(childFragmentManager, "TAG")
    }

    private fun getUserEmail(): String {
        val userName = Firebase.auth.currentUser
        var currentUserEmail = ""
        userName?.let {
            for (profile in it.providerData) {
                currentUserEmail = profile.email.toString()
            }
        }
        return currentUserEmail
    }

    private fun getUsername(): String {
        val userName = Firebase.auth.currentUser
        var currentUsername = ""
        userName?.let {
            for (profile in it.providerData) {
                currentUsername = profile.email.toString()
            }
        }

        val atIndex = currentUsername.indexOf("@")
        if (atIndex != -1) {
            return currentUsername.substring(0, atIndex)
        }

        return currentUsername
    }

    private fun fetchData() {
        val currentUserEmail = getUserEmail()
        userMealArrayList.clear()
        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val currentDay = outputDateFormat.format(Date())

        var calories = 0
        var proteinCount = 0
        var fatCount = 0
        var carbCount = 0
        var total = 0

        // Fetch user data from Firestore
        val userReference = firestoreDb.collection("users").document(currentUserEmail)
        userReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(UserData::class.java)
                    if (userData != null) {
                        val age = userData.age.toInt()
                        val gender = userData.gender
                        val height = userData.height.toLong()
                        val weight = userData.weight.toLong()

                        // Calculate BMR using the Harris-Benedict equation
                        if (gender == "Male") {
                            calories = (88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)).toInt()
                        } else if (gender == "Female") {
                            calories = (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)).toInt()
                        }
                    }
                }
            }

        // Fetch daily meal data from Firestore
        val mealReference = firestoreDb.collection("userDailyMeal")
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
            .whereEqualTo("user.email", currentUserEmail)

        mealListener = mealReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception occurred", exception)
                return@addSnapshotListener
            }

            for (dc: DocumentChange in snapshot.documentChanges) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val mealItem: DailyMealPost = dc.document.toObject(DailyMealPost::class.java)
                    val date = outputDateFormat.format(mealItem.date)
                    if (date == currentDay) {
                        calories -= mealItem.calories.toInt()
                        total += mealItem.calories.toInt()
                        proteinCount += mealItem.protein.toInt()
                        carbCount += mealItem.carbs.toInt()
                        fatCount += mealItem.fat.toInt()
                        if (!userMealArrayList.contains(mealItem)) {
                            userMealArrayList.add(0, mealItem)
                            userRecylerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
            userRecylerView.adapter = UserMealAdapter(userMealArrayList)

            setBaseGoalText { calories ->
                val remainingCalories = calories - total
                val progress = ((calories - remainingCalories).toFloat() / calories.toFloat() * 100).toInt()
                progressBar.progress = progress

                setBaseGoalText { calories ->
                    val remainingCalories = calories - total
                    val progress = ((calories - remainingCalories).toFloat() / calories.toFloat() * 100).toInt()
                    progressBar.progress = progress

                    if (remainingCalories > 0) {
                        binding.tvCalorieRemaining.text = remainingCalories.toString()
                        binding.tvRemaining.text = "Remaining"
                    } else {
                        val remainingCaloriesText = remainingCalories.toString().substring(1)
                        binding.tvCalorieRemaining.text = remainingCaloriesText
                        binding.tvRemaining.text = "Over"
                    }
                }
            }
            binding.tvReceived.text = total.toString()
            binding.tvProteinAmnt.text = proteinCount.toString()
            binding.tvCarbsAmnt.text = carbCount.toString()
            binding.tvFatAmnt.text = fatCount.toString()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MM/dd", Locale.US)
        return dateFormat.format(Date())
    }

    private fun setBaseGoalText(callback: (calories: Int) -> Unit) {
        val currentUserEmail = getUserEmail()
        val userReference = firestoreDb.collection("users").document(currentUserEmail)
        userReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(UserData::class.java)
                    if (userData != null) {
                        val age = userData.age.toInt()
                        val gender = userData.gender
                        val height = userData.height.toLong()
                        val weight = userData.weight.toLong()

                        // Calculate BMR using the Harris-Benedict equation
                        val calories = when (gender) {
                            "Male" -> (88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)).toInt()
                            "Female" -> (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)).toInt()
                            else -> 0
                        }

                        callback(calories)
                    }
                }
            }
    }
    override fun onStop() {
        super.onStop()
        mealListener.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
