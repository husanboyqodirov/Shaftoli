package softromeda.shaftoli

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.FragmentRecNotificationsBinding

class RecNotificationsFrag : Fragment() {
    private lateinit var binding: FragmentRecNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecNotificationsBinding.inflate(inflater, container, false)
        val view = binding.root
        val list = mutableListOf<RecNotifyModel>()

        val db = Firebase.firestore
        db.collection("applications")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val timestamp = document.data["date"] as Timestamp
                    val dateStamp = timestamp.toDate()
                    val dateSTR = dateStamp.toString()
                    val dateCut = dateSTR.substring(0, 10) + ", " + dateSTR.substring(30, 34)
                    if (document.data["recruiter"] == Firebase.auth.currentUser?.uid) {
                        list.add(
                            RecNotifyModel(
                                document.data["job_title"] as String,
                                document.data["applicant_name"] as String,
                                document.data["applicant_token"] as String,
                                document.data["gender"] as String,
                                dateCut
                            )
                        )
                    }
                    binding.recListView.adapter =
                        context?.let { RecNotifyAdapter(it, R.layout.rec_notify_row, list) }
                    binding.recListView.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
                        val intent = Intent(context, ApplicantView::class.java)
                        intent.putExtra("applicantToken", list[position].applicantToken)
                        intent.putExtra("documentID", document.id)
                        intent.putExtra("status", document.data["status"] as String)
                        startActivity(intent)
                        enterTransition
                    }
                }
                if (list.isNullOrEmpty())
                    binding.emptyLayout.visibility = View.VISIBLE
            }

        return view
    }
}