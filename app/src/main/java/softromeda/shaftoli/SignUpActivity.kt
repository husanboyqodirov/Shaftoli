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
import softromeda.shaftoli.databinding.SignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: SignupBinding
    var mAuth: FirebaseAuth? = null
    var collectionName: String = ""
    var userData = hashMapOf("" to "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        mAuth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener {
            it.hideKeyboard()
            createUser()
        }

        binding.tvLoginHere.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish()
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_button_1 -> {
                    binding.rdGender.visibility = View.VISIBLE
                }
                R.id.radio_button_2 -> {
                    binding.rdGender.visibility = View.GONE
                }
            }
        }
    }


    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun createUser() {
        binding.progressBar.visibility = View.VISIBLE
        val name = binding.etRegName.text.toString()
        val email = binding.etRegEmail.text.toString()
        val password = binding.etRegPass.text.toString()
        val confPassword = binding.etRegConfPass.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.progressBar.visibility = View.GONE
            binding.etRegName.error = "Full name cannot be empty."
            binding.etRegName.requestFocus()
        } else if (binding.radioButton1.isChecked) {
            if (!binding.btnMale.isChecked && !binding.btnFemale.isChecked) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Gender must be selected.", Toast.LENGTH_SHORT).show()
                binding.rdGender.requestFocus()
            }
        } else if (TextUtils.isEmpty(email)) {
            binding.progressBar.visibility = View.GONE
            binding.etRegEmail.error = "Email cannot be empty."
            binding.etRegEmail.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
            binding.progressBar.visibility = View.GONE
            binding.etRegPass.error = "Password cannot be empty."
            binding.etRegPass.requestFocus()
        } else if (password != confPassword) {
            binding.progressBar.visibility = View.GONE
            binding.etRegConfPass.error = "Passwords did not match."
            binding.etRegConfPass.requestFocus()
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
                                            var txtGender = ""
                                            txtGender = if (binding.btnMale.isChecked)
                                                "Male"
                                            else
                                                "Female"

                                            if (binding.radioButton1.isChecked) {
                                                collectionName = "job_hunters"
                                                userData = hashMapOf(
                                                    "name" to name,
                                                    "gender" to txtGender,
                                                    "address" to "",
                                                    "education" to "",
                                                    "email" to email,
                                                    "favorites" to "",
                                                    "phone" to "",
                                                    "skills" to "",
                                                    "field" to "",
                                                    "self_intro" to "",
                                                    "work_history" to "",
                                                    "password" to password,
                                                    "token" to token
                                                )
                                            } else if (binding.radioButton2.isChecked) {
                                                collectionName = "recruiters"
                                                userData = hashMapOf(
                                                    "name" to name,
                                                    "address" to "",
                                                    "email" to email,
                                                    "employees" to "",
                                                    "phone" to "",
                                                    "field" to "",
                                                    "self_intro" to "",
                                                    "password" to password,
                                                    "website" to "",
                                                    "token" to token
                                                )
                                            }

                                            db.collection(collectionName).document(token)
                                                .set(userData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this,
                                                        "You registered successfully! Please confirm your email.",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    if (binding.radioButton1.isChecked)
                                                        startActivity(
                                                            Intent(
                                                                this,
                                                                JobHunterActivity::class.java
                                                            )
                                                        )
                                                    else if (binding.radioButton2.isChecked)
                                                        startActivity(
                                                            Intent(
                                                                this,
                                                                RecruiterActivity::class.java
                                                            )
                                                        )
                                                    binding.progressBar.visibility = View.GONE
                                                    val userType = getSharedPreferences(
                                                        "shaftoli",
                                                        Context.MODE_PRIVATE
                                                    ).edit()
                                                    userType.putString("userType", collectionName)
                                                    userType.apply()
                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    binding.progressBar.visibility = View.GONE
                                                    Toast.makeText(
                                                        this,
                                                        "Registration Error.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                        }
                                    }

                            }
                        }

                } else {
                    Toast.makeText(this, "Registration Error.", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}