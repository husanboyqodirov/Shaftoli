package softromeda.shaftoli

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rec_notifications.view.*
import java.text.SimpleDateFormat


class RecNotificationsFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec_notifications, container, false)
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
                    if(document.data["recruiter"] == Firebase.auth.currentUser?.uid) {
                        list.add(RecNotifyModel(document.data["job_title"] as String, document.data["applicant_name"] as String, document.data["applicant_token"] as String, document.data["gender"] as String, dateCut))
                    }
                    view.recListView.adapter = context?.let { RecNotifyAdapter(it, R.layout.rec_notify_row, list) }
                    view.recListView.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
                        val intent = Intent(context, ApplicantView::class.java)
                        intent.putExtra("applicantToken", list[position].applicantToken)
                        intent.putExtra("documentID", document.id)
                        intent.putExtra("status", document.data["status"] as String)
                        startActivity(intent)
                        enterTransition
                    }
                }
                if (list.isNullOrEmpty())
                    view.emptyLayout.visibility = View.VISIBLE
            }

        return view
    }
}