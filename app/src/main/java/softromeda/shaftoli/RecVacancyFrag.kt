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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rec_vacancy.*
import kotlinx.android.synthetic.main.fragment_rec_vacancy.view.*

class RecApplicantsFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec_vacancy, container, false)

        val items = listOf(
            "Uzbekistan",
            "Turkmenistan",
            "Kazakhstan",
            "Kyrgyzstan",
            "Tajikistan",
            "Afghanistan"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (view.txtCountry.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        view.btnChooseCategory.setOnClickListener {
            startActivity(Intent(context, CategoryActivity::class.java))
        }

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

        view.btnAnnounce.setOnClickListener {
            it.hideKeyboard()

            context?.let { it1 ->
                MaterialAlertDialogBuilder(
                    it1,
                    R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
                )
                    .setTitle("Please confirm")
                    .setMessage("Are you sure you want to send your application to recruiter for this vacancy?")
                    .setNegativeButton("No Cancel") { _, _ ->
                    }
                    .setPositiveButton("Yes Submit") { _, _ ->
                        val txtVacancyName = view.txtVacancyName.text.toString()
                        val txtRecruiter = view.txtRecruiter.text.toString()
                        val txtAddress = view.txtAddress.text.toString()
                        val txtState = view.txtState.text.toString()
                        val txtCountry = view.txtCountryText.text.toString()
                        val txtSalary = view.txtSalary.text.toString()
                        val txtPhone = view.txtPhone.text.toString()
                        val txtEmail = view.txtEmail.text.toString()
                        val txtDescription = view.txtDescription.text.toString()
                        val txtTimeFrom = view.txtTimeFrom.text.toString()
                        val txtTimeUntil = view.txtTimeUntil.text.toString()

                        val sharedPreferences =
                            context?.getSharedPreferences("category", Activity.MODE_PRIVATE)
                        if (sharedPreferences != null) {
                            if (sharedPreferences.getString("choosenCats", "") != "") {
                                val txtCategory = sharedPreferences.getString("choosenCats", "")

                                val job = hashMapOf(
                                    "title" to txtVacancyName,
                                    "recruiter" to txtRecruiter,
                                    "address" to txtAddress,
                                    "state" to txtState,
                                    "country" to txtCountry,
                                    "salary" to txtSalary,
                                    "timeFrom" to txtTimeFrom,
                                    "timeUntil" to txtTimeUntil,
                                    "phone" to txtPhone,
                                    "email" to txtEmail,
                                    "description" to txtDescription,
                                    "skills" to txtSkills,
                                    "category" to txtCategory
                                )

                                val db = Firebase.firestore

                                db.collection("vacancies")
                                    .add(job)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Vacancy posted successfully!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val userType = context?.getSharedPreferences(
                                            "category",
                                            Context.MODE_PRIVATE
                                        )?.edit()
                                        userType?.putString("choosenCats", "")
                                        userType?.apply()
                                        Snackbar.make(
                                            view.snackViewPost,
                                            "You have successfully applied for this job! Recruiter will contact you soon.",
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

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}