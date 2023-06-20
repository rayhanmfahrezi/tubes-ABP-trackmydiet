package com.rayhan.trackmydiet.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rayhan.trackmydiet.MainActivity
import com.rayhan.trackmydiet.databinding.ActivityUserDataBinding

class UserDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDataBinding
    private lateinit var etAge: EditText
    private lateinit var spGender: Spinner
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        etAge = binding.etAge
        spGender = binding.spGender
        etWeight = binding.etWeight
        etHeight = binding.etHeight
        btnSave = binding.btnSave

        val genderOptions = arrayOf("Male", "Female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = adapter

        btnSave.setOnClickListener {
            val age: String = etAge.text.toString().trim()
            val gender: String = spGender.selectedItem.toString()
            val weight: String = etWeight.text.toString().trim()
            val height: String = etHeight.text.toString().trim()

            if (age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                saveUserDetails(age, gender, weight, height)
            }
        }
    }

    private fun saveUserDetails(age: String, gender: String, weight: String, height: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        val firestore = FirebaseFirestore.getInstance()

        if (email != null) {
            val userDetails = hashMapOf(
                "age" to age,
                "gender" to gender,
                "weight" to weight,
                "height" to height
            )

            firestore.collection("users")
                .document(email)
                .set(userDetails, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "User details saved successfully", Toast.LENGTH_SHORT).show()
                    goToMain()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save user details", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
