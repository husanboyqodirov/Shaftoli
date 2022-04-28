package softromeda.shaftoli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_applicant_view.*
import kotlinx.android.synthetic.main.fragment_hunter_profile.*
import kotlinx.android.synthetic.main.fragment_hunter_profile.profile_image
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProAddress
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProEducation
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProEmail
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProField
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProGender
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProName
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProPhone
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProSelfIntro
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProSkills
import kotlinx.android.synthetic.main.fragment_hunter_profile.txtProWorkHistory

class ApplicantView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_view)
        val applicantToken = intent.getStringExtra("applicantToken")
        val documentID = intent.getStringExtra("documentID")
        val docStatus = intent.getStringExtra("status")

        if(docStatus != "Waiting...") {
            lyConfirm.visibility = View.GONE
            if(docStatus == "Accepted!") {
                txtStatus.text = "You have accepted this applicant!"
            } else {
                txtStatus.text = "You have rejected this applicant."
            }
            lyConfirmResult.visibility = View.VISIBLE
        }

        btnModify.setOnClickListener {
            lyConfirmResult.visibility = View.GONE
            lyConfirm.visibility = View.VISIBLE
        }

        if (applicantToken != null) {
            getProfileInfo(applicantToken)
        }

        btnAccept.setOnClickListener {
            if (documentID != null) {
                takeAction("Accepted!", documentID)
            }
        }
        btnReject.setOnClickListener {
            if (documentID != null) {
                takeAction("Rejected.", documentID)
            }
        }
    }

    private fun takeAction(act: String, documentID: String) {
        val stat = hashMapOf(
            "status" to act,
        )

        val db = Firebase.firestore
        db.collection("applications").document(documentID)
            .set(stat, SetOptions.merge())
            .addOnSuccessListener {
                lyConfirm.visibility = View.GONE
                if(act == "Accepted!") {
                    txtStatus.text = "You have accepted this applicant!"
                } else {
                    txtStatus.text = "You have rejected this applicant."
                }
                lyConfirmResult.visibility = View.VISIBLE
            }
    }

    private fun getProfileInfo(appToken: String) {
        val docRef = Firebase.auth.currentUser?.let { it1 ->
            Firebase.firestore.collection("job_hunters").document(appToken)
        }
        docRef?.get()?.addOnSuccessListener { document ->
            txtProName.text = document.data?.get("name") as String
            txtProGender.text = document.data?.get("gender") as String
            if (document.data?.get("gender") as String == "Male")
                profile_image.setImageDrawable(this.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_male
                    )
                })
            else
                profile_image.setImageDrawable(this.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_female
                    )
                })
            txtProAddress.text = document.data?.get("address") as String
            txtProEducation.text = document.data?.get("education") as String
            txtProSkills.text = document.data?.get("skills") as String
            txtProField.text = document.data?.get("field") as String
            txtProEmail.text = document.data?.get("email") as String
            txtProPhone.text = document.data?.get("phone") as String
            txtProWorkHistory.text = document.data?.get("work_history") as String
            txtProSelfIntro.text = document.data?.get("self_intro") as String
        }
    }
}