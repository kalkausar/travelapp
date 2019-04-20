package com.example.kalkausar.sardifatravel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var PReqCode = 1
    var REQUESCODE = 1
    lateinit var pickedImgUrl: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressBar_reg.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()

        buttonRegister.setOnClickListener {
            buttonRegister.visibility = View.INVISIBLE
            progressBar_reg.visibility = View.VISIBLE
            val name = editTextUserName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmpassword = editTextConfirmPassword.text.toString()


            if (password.length > 8 && isValidPassword(editTextPassword.text.toString().trim())) {
                CreateUserAccount(email, password)
            }
            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || password != confirmpassword) {
                showMessage("Field tidak boleh kosong")
                buttonRegister.visibility = View.VISIBLE
                progressBar_reg.visibility = View.INVISIBLE
            } else if (password.length < 8) {
                showMessage("Password minimum 8 karakter terdiri dari angka dan huruf besar, kecil dan simbol")
                buttonRegister.visibility = View.VISIBLE
                progressBar_reg.visibility = View.INVISIBLE
            }
        }
        imageView_avareg.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission()

            } else {
                openGallery()
            }
        }
    }

    private fun isValidPassword(password: String?): Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    private fun CreateUserAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showMessage("Account Created")
                    updateUI()
                } else {
                    showMessage("Account Creation Failed" + task.exception!!.message)
                    buttonRegister.visibility = View.VISIBLE
                    progressBar_reg.visibility = View.INVISIBLE
                }
            }
    }

//    private fun updateUserInfo(name: String, pickedImgUrl: Uri, currentUser: FirebaseUser?) {
//        val mStorage = FirebaseStorage.getInstance().reference.child("users_photos")
//        val imageFilePatch = mStorage.child(pickedImgUrl.lastPathSegment!!)
//        imageFilePatch.putFile(pickedImgUrl).addOnSuccessListener {
//            imageFilePatch.downloadUrl.addOnSuccessListener { uri ->
//                val profileUpdate = UserProfileChangeRequest.Builder()
//                    .setDisplayName(name)
//                    .setPhotoUri(uri)
//                    .build()
//
//                currentUser!!.updateProfile(profileUpdate)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            showMessage("Register Complete")
//                            updateUI()
//                        }
//                    }
//            }
//        }
//    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(
                this@RegisterActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@RegisterActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                Toast.makeText(this@RegisterActivity, "please accept for required permission", Toast.LENGTH_SHORT)
                    .show()

            } else {
                ActivityCompat.requestPermissions(
                    this@RegisterActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PReqCode
                )
            }
        } else
            openGallery()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUESCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUrl = data.data
            imageView_avareg.setImageURI(pickedImgUrl)
        }
    }

    private fun updateUI() {
        val login = Intent(applicationContext, LoginActivity::class.java)
        startActivity(login)
        finish()
    }
}
