package com.example.kalkausar.sardifatravel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    internal lateinit var ImgUserPhoto: CircleImageView
    internal var PReqCode = 1
    internal var REQUESCODE = 1
    internal lateinit var pickedImgUrl:Uri

    private lateinit var auth: FirebaseAuth

    private lateinit var UserName:TextInputEditText
    private lateinit var Email:TextInputEditText
    private lateinit var Password:TextInputEditText
    private lateinit var ConfirmPassword:TextInputEditText

    private lateinit var loadingProgress:ProgressBar
    private lateinit var buttonRegister:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        ImgUserPhoto = findViewById(R.id.imageView_avareg)

        UserName = findViewById(R.id.editTextUserName)
        Email = findViewById(R.id.editTextEmail)
        Password = findViewById(R.id.editTextPassword)
        ConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        loadingProgress = findViewById(R.id.progressBar_reg)
        buttonRegister = findViewById(R.id.buttonRegister)
        loadingProgress.setVisibility(View.INVISIBLE)

        buttonRegister.setOnClickListener {
            buttonRegister.visibility = View.INVISIBLE
            loadingProgress.visibility = View.VISIBLE
            val name = UserName.text.toString()
            val email = Email.text.toString()
            val password = Password.text.toString()
            val confirmpassword = ConfirmPassword.text.toString()


            if (password.length > 8 && isValidPassword(Password.text.toString().trim { it <= ' ' })) {
                CreateUserAccount(email, name, password)
            }
            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || password != confirmpassword) {
                showMessage("Field tidak boleh kosong")
                buttonRegister.visibility = View.VISIBLE
                loadingProgress.visibility = View.INVISIBLE
            } else {
                showMessage("Password minimum 8 karakter terdiri dari angka dan huruf besar, kecil dan simbol")
                buttonRegister.visibility = View.VISIBLE
                loadingProgress.visibility = View.INVISIBLE
            }
        }

        ImgUserPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission()

            } else {
                openGallery()
            }
        }
    }

    fun isValidPassword(password: String?) : Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    fun CreateUserAccount(email: String, name: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    showMessage("Account Created")
                    updateUserInfo(name, pickedImgUrl, auth.getCurrentUser())
                } else {
                    showMessage("Account Creation Failed" + task.exception!!.message)
                    buttonRegister.visibility = View.VISIBLE
                    loadingProgress.visibility = View.INVISIBLE
                }
            })
    }

    fun updateUserInfo(name: String, pickedImgUrl: Uri, currentUser: FirebaseUser?) {
        val mStorage = FirebaseStorage.getInstance().getReference().child("users_photos")
        val imageFilePatch = mStorage.child(pickedImgUrl.lastPathSegment)
        imageFilePatch.putFile(pickedImgUrl).addOnSuccessListener(OnSuccessListener<Any> {
            imageFilePatch.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(uri)
                    .build()

                currentUser!!.updateProfile(profileUpdate)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showMessage("Register Complete")
                            updateUI()
                        }
                    }
            })
        })
    }

    fun updateUI() {
        val login = Intent(applicationContext, LoginActivity::class.java)
        startActivity(login)
        finish()
    }

    fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUESCODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUrl = data.data
            ImgUserPhoto.setImageURI(pickedImgUrl)
        }
    }
}
