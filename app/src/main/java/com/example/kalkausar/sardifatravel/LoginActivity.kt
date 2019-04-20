package com.example.kalkausar.sardifatravel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    internal var doubleBackToExitPressedOnce = false
    private lateinit var auth: FirebaseAuth

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

        auth = FirebaseAuth.getInstance()

        progressBar_login.visibility = View.INVISIBLE
        buttonLogin.setOnClickListener {
            progressBar_login.visibility = View.VISIBLE
            buttonLogin.visibility = View.INVISIBLE

            val mail = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Please Verify All Field")
                buttonLogin.visibility = View.VISIBLE
                progressBar_login.visibility = View.INVISIBLE
            } else {
                singnIn(mail, password)
            }
        }

        textViewCreateAccount.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }
    }

    private fun singnIn(mail: String, password: String) {

        auth.signInWithEmailAndPassword(mail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressBar_login.visibility = View.INVISIBLE
                    buttonLogin.visibility = View.VISIBLE
                    updateUI()
                } else {
                    showMessage("Login Failed")
                    buttonLogin.visibility = View.VISIBLE
                    progressBar_login.visibility = View.INVISIBLE
                }
            }
    }

    private fun updateUI() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showMessage(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI()
        }
    }
}
