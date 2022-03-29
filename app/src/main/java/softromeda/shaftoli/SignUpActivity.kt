package softromeda.shaftoli

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.signup.*

class SignUpActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var collectionName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.signup)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        mAuth = FirebaseAuth.getInstance()
        btnRegister.setOnClickListener {
            it.hideKeyboard()
            createUser()
        }

        tvLoginHere.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish()
        }
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun createUser() {
        progressBar.visibility = View.VISIBLE
        val name = etRegName.text.toString()
        val email = etRegEmail.text.toString()
        val password: String = etRegPass.text.toString()
        val confPassword: String = etRegConfPass.text.toString()
        if (TextUtils.isEmpty(name)) {
            progressBar.visibility = View.GONE
            etRegName.error = "Full name cannot be empty."
            etRegName.requestFocus()
        } else if (TextUtils.isEmpty(email)) {
            progressBar.visibility = View.GONE
            etRegEmail.error = "Email cannot be empty."
            etRegEmail.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            progressBar.visibility = View.GONE
            etRegPass.error = "Password cannot be empty."
            etRegPass.requestFocus()
        } else if (password != confPassword) {
            progressBar.visibility = View.GONE
            etRegConfPass.error = "Passwords did not match."
            etRegConfPass.requestFocus()
        } else {
            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val db = Firebase.firestore
                                            val token = user.uid
                                            val job_hunter = hashMapOf(
                                                "name" to name,
                                                "email" to email,
                                                "password" to password,
                                                "token" to token
                                            )

                                            if (radio_button_1.isChecked)
                                                collectionName = "job_hunter"
                                            else if (radio_button_2.isChecked)
                                                collectionName = "recruiter"

                                            db.collection(collectionName).document(token)
                                                .set(job_hunter)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "You registered successfully! Please confirm your email.", Toast.LENGTH_LONG).show()
                                                    if (radio_button_1.isChecked)
                                                        startActivity(Intent(this, JobHunterActivity::class.java))
                                                    else if (radio_button_2.isChecked)
                                                        startActivity(Intent(this, RecruiterActivity::class.java))
                                                    progressBar.visibility = View.GONE
                                                    val userType = getSharedPreferences("UserType", Context.MODE_PRIVATE).edit()
                                                    userType.putString("Type", collectionName)
                                                    userType.apply()
                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    progressBar.visibility = View.GONE
                                                    Toast.makeText(this, "Registration Error.", Toast.LENGTH_SHORT).show()
                                                }

                                        }
                                    }

                            }
                        }

                } else {
                    Toast.makeText(this, "Registration Error.", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}