package com.rayhan.trackmydiet.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.rayhan.trackmydiet.MainActivity
import com.rayhan.trackmydiet.databinding.ActivityLoginBinding

private const val TAG= "LoginActivity"
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initView() {
        etEmail= binding.etEmail
        etPassword = binding.etPassword

        btnLogin = binding.btnLogin
        tvRegister = binding.tvRegister

        val auth = FirebaseAuth.getInstance()


        if(auth.currentUser!= null){
            goToMain()
        }

        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()


            if(email.isBlank()|| password.isBlank()) {
                btnLogin.isEnabled = true
                Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                btnLogin.isEnabled = true
                if(task.isSuccessful){
                    goToMain()
                }else{
                    Log.i(TAG, "Login Failed", task.exception)
                    Toast.makeText(this, "Unable to login", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}