package softromeda.shaftoli

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.ActivityJobViewBinding

class JobViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobViewBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(binding.root)
        val jobID = intent.getStringExtra("jobID")

        val db = Firebase.firestore

        var applicants = ""
        var rec_token = ""
        var exp_years = ""
        var appGender = ""

        val docRef = jobID?.let { db.collection("vacancies").document(it) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                binding.txtJobTitle.text = document.data?.get("title") as String
                binding.txtJobRecruiter.text = document.data?.get("recruiter") as String
                val address = document.data!!["address"] as String + ", " +
                        document.data!!["state"] as String + ", " +
                        document.data!!["country"] as String
                binding.txtJobAddress.text = address
                val time =
                    document.data!!["timeFrom"] as String + " ~ " + document.data!!["timeUntil"] as String
                binding.txtJobSalary.text = "$" + document.data?.get("salary") as String
                binding.txtJobTime.text = time
                binding.txtJobCategory.text = document.data?.get("category") as String
                binding.txtJobDescription.text = document.data?.get("description") as String
                binding.txtJobSkills.text = document.data?.get("skills") as String
                binding.txtJobPhone.text = document.data?.get("phone") as String
                binding.txtJobEmail.text = document.data?.get("email") as String
                binding.txtJobEducation.text = document.data?.get("education") as String
                applicants = document.data?.get("applicants") as String
                rec_token = document.data?.get("rec_token") as String
                binding.pbJobView.visibility = View.GONE
                binding.lyJobView.visibility = View.VISIBLE
            }
        }

        binding.btnCallJobView.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + binding.txtJobPhone.text)
            startActivity(dialIntent)
        }

        binding.btnEmailJobView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, binding.txtJobEmail.text)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Job Application")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Hello Sir/Madam.\n\nI am writing to apply for a job vacancy you posted on Shaftoli platform."
            )
            startActivity(Intent.createChooser(intent, "Choose Email Client"))
        }

        binding.btnApplyJob.setOnClickListener {
            val dialogView: View = View.inflate(this, R.layout.activity_apply, null)
            val dlg = AlertDialog.Builder(this)

            val docRef2 = Firebase.auth.currentUser?.let { it1 ->
                db.collection("job_hunters").document(
                    it1.uid
                )
            }
            val editName = dialogView.findViewById<TextInputEditText>(R.id.editName)
            val radio_button_1 = dialogView.findViewById<RadioButton>(R.id.radio_button_1)
            val radio_button_2 = dialogView.findViewById<RadioButton>(R.id.radio_button_2)
            val editEducation = dialogView.findViewById<TextInputEditText>(R.id.editEducation)
            val editSkills = dialogView.findViewById<TextInputEditText>(R.id.editSkills)
            val editEmail = dialogView.findViewById<TextInputEditText>(R.id.editEmail)
            val editPhone = dialogView.findViewById<TextInputEditText>(R.id.editPhone)
            val chkExperience = dialogView.findViewById<SwitchMaterial>(R.id.chkExperience)
            val lyExperience = dialogView.findViewById<LinearLayout>(R.id.lyExperience)
            val txtYears = dialogView.findViewById<TextView>(R.id.txtYears)
            val slYears = dialogView.findViewById<Slider>(R.id.slYears)

            docRef2?.get()?.addOnSuccessListener {
                editName.setText(it.data?.get("name") as String)
                appGender = it.data?.get("gender") as String
                if (appGender == "Male")
                    radio_button_1.isChecked = true
                else
                    radio_button_2.isChecked = true
                editEducation.setText(it.data?.get("education") as String)
                editSkills.setText(it.data?.get("skills") as String)
                editEmail.setText(it.data?.get("email") as String)
                editPhone.setText(it.data?.get("phone") as String)
            }
            chkExperience.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    lyExperience.visibility = View.VISIBLE
                    exp_years = txtYears.text.toString()
                } else {
                    lyExperience.visibility = View.GONE
                    exp_years = "None"
                }
            }

            slYears.addOnChangeListener { _, _, _ ->
                txtYears.text = slYears.value.toInt().toString() + " years"
                exp_years = txtYears.text.toString()
            }

            dlg.setTitle("Application")
            dlg.setView(dialogView)
            dlg.setPositiveButton("Yes, Apply") { _, _ ->
                run {
                    Firebase.auth.currentUser?.let { it1 ->
                        if (!(applicants.contains(it1.uid, ignoreCase = false))) {
                            if (jobID != null) {
                                db.collection("vacancies").document(jobID)
                                    .set(
                                        hashMapOf(
                                            "applicants" to "${it1.uid}$applicants",
                                            "new_applicant" to "true"
                                        ),
                                        SetOptions.merge()
                                    )
                                    .addOnSuccessListener {
                                        db.collection("applications").document()
                                            .set(
                                                hashMapOf(
                                                    "job_id" to jobID,
                                                    "recruiter" to rec_token,
                                                    "rec_name" to binding.txtJobRecruiter.text.toString(),
                                                    "applicant_token" to it1.uid,
                                                    "applicant_name" to editName.text.toString(),
                                                    "gender" to appGender,
                                                    "job_title" to binding.txtJobTitle.text.toString(),
                                                    "status" to "Waiting...",
                                                    "date" to FieldValue.serverTimestamp()
                                                )
                                            )
                                            .addOnSuccessListener {
                                                applicants = it1.uid + applicants
                                                Snackbar.make(
                                                    binding.contextV,
                                                    "You have successfully applied for this job! Recruiter will contact you soon.",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                            }
                        } else {
                            Snackbar.make(
                                binding.contextV,
                                "You have already send an application for this vacancy.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                    }

                }
            }
            dlg.setNegativeButton("Cancel", null)
            dlg.show()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}