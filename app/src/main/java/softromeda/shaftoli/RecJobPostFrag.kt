package softromeda.shaftoli

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rec_job_post.view.*

class RecJobPostFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rec_job_post, container, false)

        view.recyclerJobPosts.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )
        val db = Firebase.firestore
        db.collection("vacancies")
            .get()
            .addOnSuccessListener { result ->
                val precautionsList = ArrayList<Model>()
                for (document in result) {
                    if (document.data["rec_token"] == Firebase.auth.currentUser?.uid) {
                        precautionsList.add(
                            Model(
                                document.data["title"] as String,
                                document.data["recruiter"] as String,
                                document.data["address"] as String + ", " +
                                        document.data["state"] as String + ", " +
                                        document.data["country"] as String,
                                document.data["salary"] as String,
                                document.data["timeFrom"] as String + " ~ " + document.data["timeUntil"] as String,
                                document.data["education"] as String,
                                document.data["category"] as String,
                                "Deadline:  " + document.data["deadline"] as String,
                                ""
                            )
                        )
                    }

                }
                val precautionsAdapter = JobPostsAdapter(precautionsList)
                view.recyclerJobPosts.adapter = precautionsAdapter
            }
            .addOnFailureListener { exception ->
            }

        return view
    }

}