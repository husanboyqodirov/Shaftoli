package softromeda.shaftoli

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_job_view.*

class JobViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_job_view)
        val jobID = intent.getStringExtra("jobID")

        val db = Firebase.firestore

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

                pbJobView.visibility = View.GONE
                lyJobView.visibility = View.VISIBLE
            }
        }?.addOnFailureListener {
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
            startActivity(Intent(this, ApplyActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}