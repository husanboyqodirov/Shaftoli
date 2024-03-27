package softromeda.shaftoli

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.ActivityApplicantViewBinding

class ApplicantView : AppCompatActivity() {
    private lateinit var binding: ActivityApplicantViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicantViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val applicantToken = intent.getStringExtra("applicantToken")
        val documentID = intent.getStringExtra("documentID")
        val docStatus = intent.getStringExtra("status")

        if (docStatus != "Waiting...") {
            binding.lyConfirm.visibility = View.GONE
            if (docStatus == "Accepted!") {
                binding.txtStatus.text = "You have accepted this applicant!"
            } else {
                binding.txtStatus.text = "You have rejected this applicant."
            }
            binding.lyConfirmResult.visibility = View.VISIBLE
        }

        binding.btnModify.setOnClickListener {
            binding.lyConfirmResult.visibility = View.GONE
            binding.lyConfirm.visibility = View.VISIBLE
        }

        if (applicantToken != null) {
            getProfileInfo(applicantToken)
        }

        binding.btnAccept.setOnClickListener {
            if (documentID != null) {
                takeAction("Accepted!", documentID)
            }
        }
        binding.btnReject.setOnClickListener {
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
                binding.lyConfirm.visibility = View.GONE
                if (act == "Accepted!") {
                    binding.txtStatus.text = "You have accepted this applicant!"
                } else {
                    binding.txtStatus.text = "You have rejected this applicant."
                }
                binding.lyConfirmResult.visibility = View.VISIBLE
            }
    }

    private fun getProfileInfo(appToken: String) {
        val docRef = Firebase.auth.currentUser?.let { it1 ->
            Firebase.firestore.collection("job_hunters").document(appToken)
        }
        docRef?.get()?.addOnSuccessListener { document ->
            binding.txtProName.text = document.data?.get("name") as String
            binding.txtProGender.text = document.data?.get("gender") as String
            if (document.data?.get("gender") as String == "Male")
                binding.profileImage.setImageDrawable(this.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_male
                    )
                })
            else
                binding.profileImage.setImageDrawable(this.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_female
                    )
                })
            binding.txtProAddress.text = document.data?.get("address") as String
            binding.txtProEducation.text = document.data?.get("education") as String
            binding.txtProSkills.text = document.data?.get("skills") as String
            binding.txtProField.text = document.data?.get("field") as String
            binding.txtProEmail.text = document.data?.get("email") as String
            binding.txtProPhone.text = document.data?.get("phone") as String
            binding.txtProWorkHistory.text = document.data?.get("work_history") as String
            binding.txtProSelfIntro.text = document.data?.get("self_intro") as String
        }
    }
}