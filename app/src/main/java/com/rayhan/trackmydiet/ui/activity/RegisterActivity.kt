package com.rayhan.trackmydiet.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.rayhan.trackmydiet.databinding.ActivityRegisterBinding

private const val TAG ="RegisterActivity"
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var etUsername : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var etConfirmPassword : EditText
    private lateinit var btnRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        etUsername = binding.etUsername
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        etConfirmPassword = binding.etConfirmPassword
        btnRegister = binding.btnRegister

        btnRegister.setOnClickListener {
            val username: String = etUsername.text.toString().trim()
            val email: String = etEmail.text.toString().trim()
            val password: String = etPassword.text.toString().trim()
            val confirm: String = etConfirmPassword.text.toString().trim()
            btnRegister.isEnabled = false

            if(username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()){
                Toast.makeText(this, "Missing Infromation", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(confirm == password && email.contains("@")){
                checkForValidEmail(email, password)

            }else{
                Toast.makeText(this, "Invalid email or password do not match", Toast.LENGTH_LONG).show()
            }
            btnRegister.isEnabled = true
        }
    }

    private fun goToUserData() {
        val intent = Intent(this, UserDataActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createUserAccount(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                btnRegister.isEnabled = true
                if (task.isSuccessful) {
                    Log.i(TAG, "created user Successfully")
                    goToUserData()

                } else {
                    Log.e(TAG, "failed to create user", task.exception)
                    Toast.makeText(this, "Unable to create account", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkForValidEmail(email:String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task->
            val isNewUser:Boolean = task.result.signInMethods!!.isEmpty()
            if(isNewUser){
                createUserAccount(email, password)
            }else{
                Toast.makeText(this, "Can not create account", Toast.LENGTH_SHORT).show()
            }
        }
    }
}