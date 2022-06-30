package softromeda.shaftoli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rec_notifications.view.*

class HunterNotificationsFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hunter_notifications, container, false)

        val list = mutableListOf<HunterApplicationsModel>()

        val db = Firebase.firestore
        db.collection("applications")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.data["applicant_token"] != Firebase.auth.currentUser?.uid)
                        view.emptyLayout.visibility = View.GONE
                    val timestamp = document.data["date"] as Timestamp
                    val dateStamp = timestamp.toDate()
                    val dateSTR = dateStamp.toString()
                    val dateCut = dateSTR.substring(0, 10) + ", " + dateSTR.substring(30, 34)

                    list.add(
                        HunterApplicationsModel(
                            document.data["job_title"] as String,
                            document.data["rec_name"] as String,
                            dateCut,
                            document.data["status"] as String,
                        )
                    )
                    view.recListView.adapter = context?.let {
                        HunterApplicationsAdapter(
                            it,
                            R.layout.hunter_notify_row,
                            list
                        )
                    }

                }
                if (list.isNullOrEmpty())
                    view.emptyLayout.visibility = View.VISIBLE
            }

        return view
    }

}