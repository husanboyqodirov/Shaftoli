package softromeda.shaftoli

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rec_profile.*
import kotlinx.android.synthetic.main.fragment_rec_vacancy.*
import kotlinx.android.synthetic.main.fragment_rec_vacancy.view.*
import java.security.Timestamp
import java.time.Instant.now
import java.time.LocalDateTime.now
import java.time.MonthDay.now

class RecVacancyFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec_vacancy, container, false)

        val countries = listOf(
            "Uzbekistan",
            "Turkmenistan",
            "Kazakhstan",
            "Kyrgyzstan",
            "Tajikistan",
            "Afghanistan"
        )
        var adapter = ArrayAdapter(requireContext(), R.layout.list_item, countries)
        (view.txtCountry.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        val levels = listOf(
            "Associate",
            "Bachelor's",
            "Master's",
            "Doctoral",
            "Not needed"
        )
        adapter = ArrayAdapter(requireContext(), R.layout.list_item, levels)
        (view.txtLevel.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        view.btnChooseCategory.setOnClickListener {
            startActivity(Intent(context, CategoryActivity::class.java))
        }

        getProfileInfo()

        view.txtTimeFrom.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(8)
                    .setMinute(0)
                    .build()
            fragmentManager?.let { it1 -> picker.show(it1, "tag") };

            picker.addOnPositiveButtonClickListener {
                val chosenTime = picker.hour.toString() + ":" + picker.minute.toString()
                view.txtTimeFrom.text = chosenTime
            }
        }

        view.txtTimeUntil.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(8)
                    .setMinute(0)
                    .build()
            fragmentManager?.let { it1 -> picker.show(it1, "tag") };

            picker.addOnPositiveButtonClickListener {
                val chosenTime = picker.hour.toString() + ":" + picker.minute.toString()
                view.txtTimeUntil.text = chosenTime
            }
        }

        var txtDeadline = ""

        view.btnDeadline.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Vacancy Deadline ")
                    .build()

            fragmentManager?.let { it1 -> datePicker.show(it1, "Tag") }

            datePicker.addOnPositiveButtonClickListener {
                txtDeadline = datePicker.headerText
                Toast.makeText(context, txtDeadline, Toast.LENGTH_SHORT).show()
            }
        }

        view.btnAnnounce.setOnClickListener {
            it.hideKeyboard()

            context?.let { it1 ->
                MaterialAlertDialogBuilder(
                    it1,
                    R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
                )
                    .setTitle("Please confirm")
                    .setMessage("Are you sure you want post this vacancy?")
                    .setNegativeButton("No Cancel") { _, _ ->
                    }
                    .setPositiveButton("Yes Post") { _, _ ->
                        val txtVacancyName = view.txtVacancyName.text.toString()
                        val txtRecruiter = view.txtRecruiter.text.toString()
                        val txtAddress = view.txtAddress.text.toString()
                        val txtState = view.txtState.text.toString()
                        val txtCountry = view.txtCountryText.text.toString()
                        val txtSalary = view.txtSalary.text.toString()
                        val txtEducation = view.txtDegreeLevel.text.toString()
                        val txtTimeFrom = view.txtTimeFrom.text.toString()
                        val txtTimeUntil = view.txtTimeUntil.text.toString()
                        val txtPhone = view.txtPhone.text.toString()
                        val txtEmail = view.txtEmail.text.toString()
                        val txtDescription = view.txtDescription.text.toString()
                        val txtSkill = view.txtSkills.text.toString()

                        val sharedPreferences =
                            context?.getSharedPreferences("shaftoli", Activity.MODE_PRIVATE)
                        if (sharedPreferences != null) {
                            if (sharedPreferences.getString("chosenCats", "") != "") {
                                val txtCategory = sharedPreferences.getString("chosenCats", "")
                                val txtField = sharedPreferences.getString("jobField", "")

                                val db = Firebase.firestore

                                db.collection("vacancies")
                                    .add(mapOf(
                                        "title" to txtVacancyName,
                                        "recruiter" to txtRecruiter,
                                        "address" to txtAddress,
                                        "applicants" to "",
                                        "field" to txtField,
                                        "state" to txtState,
                                        "country" to txtCountry,
                                        "salary" to txtSalary,
                                        "education" to txtEducation,
                                        "timeFrom" to txtTimeFrom,
                                        "timeUntil" to txtTimeUntil,
                                        "phone" to txtPhone,
                                        "email" to txtEmail,
                                        "description" to txtDescription,
                                        "skills" to txtSkill,
                                        "category" to txtCategory,
                                        "deadline" to txtDeadline,
                                        "rec_token" to Firebase.auth.currentUser?.uid,
                                        "created" to FieldValue.serverTimestamp()
                                    ))
                                    .addOnSuccessListener {
                                        val userType = context?.getSharedPreferences(
                                            "shaftoli",
                                            Context.MODE_PRIVATE
                                        )?.edit()
                                        userType?.putString("chosenCats", "")
                                        userType?.apply()
                                        Snackbar.make(
                                            view.snackViewPost,
                                            "Vacancy is posted successfully!",
                                            Snackbar.LENGTH_LONG
                                        )
                                            .setAction("OK") {}.show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Error in creating announcement.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please choose category.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .show()
            }
        }
        return view
    }

    private fun getProfileInfo() {
        val docRef = Firebase.auth.currentUser?.let { it1 ->
            Firebase.firestore.collection("recruiters").document(
                it1.uid
            )
        }
        docRef?.get()?.addOnSuccessListener { document ->
            txtRecruiter.setText(document.data?.get("name") as String)
            txtEmail.setText(document.data?.get("email") as String)
            txtPhone.setText(document.data?.get("phone") as String)
        }
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}