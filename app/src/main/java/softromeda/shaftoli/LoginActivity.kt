package softromeda.shaftoli

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.signup.*


class LoginActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            it.hideKeyboard()
            loginUser()
        }
        tvRegisterHere.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun loginUser() {
        prBarLogin.visibility = View.VISIBLE
        val email = etLoginEmail.text.toString()
        val password: String = etLoginPass.text.toString()
        if (TextUtils.isEmpty(email)) {
            progressBar.visibility = View.GONE
            etLoginEmail.error = "Email cannot be empty."
            etLoginEmail.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            progressBar.visibility = View.GONE
            etLoginPass.error = "Password cannot be empty."
            etLoginPass.requestFocus()
        } else {
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var token = Firebase.auth.currentUser?.uid
                    token = token.toString()
                    val userType = getSharedPreferences("shaftoli", Context.MODE_PRIVATE).edit()
                    Firebase.firestore.collection("job_hunters")
                    .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                if(document.data["token"] == token) {
                                    startActivity(Intent(this, JobHunterActivity::class.java))
                                    userType.putString("userType", "job_hunter")
                                    userType.putString("myField", document.data["field"] as String)
                                    userType.apply()
                                    finish()
                                    return@addOnSuccessListener
                                }
                            }
                            startActivity(Intent(this, RecruiterActivity::class.java))
                            userType.putString("userType", "recruiter")
                            userType.apply()
                            finish()
                        }
                    prBarLogin.visibility = View.GONE
                } else {
                    Toast.makeText(this, "Please check your email and password.", Toast.LENGTH_LONG).show()
                    prBarLogin.visibility = View.GONE
                }
            }
        }
    }
}