package com.example.kalkausar.sardifatravel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    internal var doubleBackToExitPressedOnce = false
    //Declaration EditTexts
    private lateinit var Email: TextInputEditText
    private lateinit var Password: TextInputEditText

    //Declaration
    private lateinit var buttonLogin: Button
    private lateinit var textViewCreateAccount: TextView
    private lateinit var loginProgress: ProgressBar
    private lateinit var mAuth: FirebaseAuth

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Pleas click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Email = findViewById(R.id.editTextEmail)
        Password = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewCreateAccount = findViewById(R.id.textViewCreateAccount)
        loginProgress = findViewById(R.id.progressBar_login)
        mAuth = FirebaseAuth.getInstance()

        loginProgress.visibility = View.INVISIBLE
        buttonLogin.setOnClickListener {
            loginProgress.visibility = View.VISIBLE
            buttonLogin.visibility = View.INVISIBLE

            val mail = Email.text.toString()
            val password = Password.text.toString()

            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Please Verify All Field")
                buttonLogin.visibility = View.VISIBLE
                loginProgress.visibility = View.INVISIBLE
            } else {
                singnIn(mail, password)
            }
        }

        textViewCreateAccount.setOnClickListener {
            val regist = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(regist)
            finish()
        }
    }

    private fun singnIn(mail: String, password: String) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginProgress.visibility = View.INVISIBLE
                buttonLogin.visibility = View.VISIBLE
                updateUI()
            } else {
                task.exception?.message?.let { showMessage(it) }
                buttonLogin.visibility = View.VISIBLE
                loginProgress.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateUI() {
        val Home = Intent(applicationContext, MainActivity::class.java)
        startActivity(Home)
        finish()
    }

    private fun showMessage(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser

        if (user != null) {
            updateUI()
        }
    }
}
