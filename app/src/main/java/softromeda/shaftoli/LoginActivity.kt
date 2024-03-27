package softromeda.shaftoli

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.LoginBinding


class LoginActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            it.hideKeyboard()
            loginUser()
        }
        binding.tvRegisterHere.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun loginUser() {
        binding.prBarLogin.visibility = View.VISIBLE
        val email = binding.etLoginEmail.text.toString()
        val password: String = binding.etLoginPass.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.prBarLogin.visibility = View.GONE
            binding.etLoginEmail.error = "Email cannot be empty."
            binding.etLoginEmail.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            binding.prBarLogin.visibility = View.GONE
            binding.etLoginPass.error = "Password cannot be empty."
            binding.etLoginPass.requestFocus()
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
                                if (document.data["token"] == token) {
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
                    binding.prBarLogin.visibility = View.GONE
                } else {
                    Toast.makeText(this, "Please check your email and password.", Toast.LENGTH_LONG)
                        .show()
                    binding.prBarLogin.visibility = View.GONE
                }
            }
        }
    }
}