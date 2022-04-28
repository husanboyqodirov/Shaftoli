package softromeda.shaftoli

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_apply.*
import kotlinx.android.synthetic.main.activity_apply.view.*
import kotlinx.android.synthetic.main.activity_job_view.*

class JobViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_job_view)
        val jobID = intent.getStringExtra("jobID")

        val db = Firebase.firestore

        var applicants= ""
        var rec_token = ""
        var exp_years = ""
        var appGender = ""

        val docRef = jobID?.let { db.collection("vacancies").document(it) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                txtJobTitle.text = document.data?.get("title") as String
                txtJobRecruiter.text = document.data?.get("recruiter") as String
                val address = document.data!!["address"] as String + ", " +
                        document.data!!["state"] as String + ", " +
                        document.data!!["country"] as String
                txtJobAddress.text = address
                val time = document.data!!["timeFrom"] as String + " ~ " + document.data!!["timeUntil"] as String
                txtJobSalary.text = "$" + document.data?.get("salary") as String
                txtJobTime.text = time
                txtJobCategory.text = document.data?.get("category") as String
                txtJobDescription.text = document.data?.get("description") as String
                txtJobSkills.text = document.data?.get("skills") as String
                txtJobPhone.text = document.data?.get("phone") as String
                txtJobEmail.text = document.data?.get("email") as String
                txtJobEducation.text = document.data?.get("education") as String
                applicants = document.data?.get("applicants") as String
                rec_token = document.data?.get("rec_token") as String
                pbJobView.visibility = View.GONE
                lyJobView.visibility = View.VISIBLE
            }
        }

        btnCallJobView.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + txtJobPhone.text)
            startActivity(dialIntent)
        }

        btnEmailJobView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, txtJobEmail.text)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Job Application")
            intent.putExtra(Intent.EXTRA_TEXT, "Hello Sir/Madam.\n\nI am writing to apply for a job vacancy you posted on Shaftoli platform.")
            startActivity(Intent.createChooser(intent, "Choose Email Client"))
        }

        btnApplyJob.setOnClickListener {
            val dialogView: View = View.inflate(this, R.layout.activity_apply, null)
            val dlg = AlertDialog.Builder(this)

            val docRef2 = Firebase.auth.currentUser?.let { it1 ->
                db.collection("job_hunters").document(
                    it1.uid
                )
            }
            docRef2?.get()?.addOnSuccessListener {
                dialogView.editName.setText(it.data?.get("name") as String)
                appGender = it.data?.get("gender") as String
                if (appGender == "Male")
                    dialogView.radio_button_1.isChecked = true
                else
                    dialogView.radio_button_2.isChecked = true
                dialogView.editEducation.setText(it.data?.get("education") as String)
                dialogView.editSkills.setText(it.data?.get("skills") as String)
                dialogView.editEmail.setText(it.data?.get("email") as String)
                dialogView.editPhone.setText(it.data?.get("phone") as String)
            }
            dialogView.chkExperience.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    dialogView.lyExperience.visibility = View.VISIBLE
                    exp_years = dialogView.txtYears.text.toString()
                }
                else {
                    dialogView.lyExperience.visibility = View.GONE
                    exp_years = "None"
                }
            }

            dialogView.slYears.addOnChangeListener { _, _, _ ->
                dialogView.txtYears.text = dialogView.slYears.value.toInt().toString() + " years"
                exp_years = dialogView.txtYears.text.toString()
            }

            dlg.setTitle("Application")
            dlg.setView(dialogView)
            dlg.setPositiveButton("Yes, Apply") { _, _ ->
                run {
                        Firebase.auth.currentUser?.let { it1 ->
                            if(!(applicants.contains(it1.uid, ignoreCase = false))) {
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
                                                        "rec_name" to txtJobRecruiter.text.toString(),
                                                        "applicant_token" to it1.uid,
                                                        "applicant_name" to dialogView.editName.text.toString(),
                                                        "gender" to appGender,
                                                        "job_title" to txtJobTitle.text.toString(),
                                                        "status" to "Waiting...",
                                                        "date" to FieldValue.serverTimestamp()
                                                    )
                                                )
                                                .addOnSuccessListener {
                                                    applicants = it1.uid + applicants
                                                    Snackbar.make(
                                                        contextV,
                                                        "You have successfully applied for this job! Recruiter will contact you soon.",
                                                        Snackbar.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                }
                            } else {
                                Snackbar.make(
                                    contextV,
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