package softromeda.shaftoli

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hunter_favs.*
import kotlinx.android.synthetic.main.fragment_hunter_favs.view.*
import kotlinx.android.synthetic.main.item_posts.*

class HunterFavoritesFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hunter_favs, container, false)

        view.hunterJobFavs.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, true
        )
        getFavorites()
        return view
    }

    fun getFavorites() {
        val db = Firebase.firestore
        val docRef = Firebase.auth.currentUser?.let { it1 ->
            db.collection("job_hunters").document(
                it1.uid
            )
        }
        docRef?.get()?.addOnSuccessListener { document ->
            val jobFavs = document.data?.get("favorites") as String
            var i = 0
            var keepLoop = true
            val jobIDs = mutableListOf<String>()
            if (jobFavs.isNotEmpty()) {
                while (keepLoop) {
                    jobIDs.add(jobFavs.substring(i, i + 20))
                    if (jobFavs.length > i + 20)
                        i += 20
                    else
                        keepLoop = false
                }
            }
            val precautionsList = ArrayList<Model>()

            for (x in 0 until jobIDs.size) {
                val docReff = db.collection("vacancies").document(jobIDs[x])
                docReff.get()
                    .addOnSuccessListener {
                        precautionsList.add(
                            Model(
                                it.data!!["title"] as String,
                                it.data!!["recruiter"] as String,
                                it.data!!["address"] as String + ", " +
                                        it.data!!["state"] as String + ", " +
                                        it.data!!["country"] as String,
                                it.data!!["salary"] as String,
                                it.data!!["timeFrom"] as String + " ~ " + it.data!!["timeUntil"] as String,
                                it.data!!["education"] as String,
                                it.data!!["category"] as String,
                                "Deadline:  " + it.data!!["deadline"] as String,
                                "favs"
                            )
                        )
                        val precautionsAdapter = JobPostsAdapter(precautionsList)
                        hunterJobFavs.adapter = precautionsAdapter

                        val adapter = JobPostsAdapter(precautionsList)
                        adapter.itemClick = object : JobPostsAdapter.ItemClick {
                            override fun onClick(view: View, position: Int) {
                                val intent = Intent(context, JobViewActivity::class.java)
                                intent.putExtra("jobID", jobIDs[position])
                                startActivity(intent)
                                enterTransition
                            }
                        }

                        adapter.itemLongLick =
                            object : JobPostsAdapter.ItemLongClick {
                                override fun onLongClick(view: View, position: Int) {
                                    context?.let {
                                        MaterialAlertDialogBuilder(
                                            it,
                                            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
                                        )
                                            .setMessage("Remove this vacancy from favorites?")
                                            .setNegativeButton("No") { _, _ ->
                                            }
                                            .setPositiveButton("Yes") { _, _ ->
                                                jobIDs.removeAt(position)
                                                var finalFavs: String = ""
                                                for(s in jobIDs)
                                                    finalFavs += s
                                                Firebase.auth.currentUser?.let { it1 ->
                                                    db.collection("job_hunters").document(it1.uid)
                                                        .set(
                                                            hashMapOf("favorites" to finalFavs),
                                                            SetOptions.merge()
                                                        )
                                                        .addOnSuccessListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Vacancy removed from favorites.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            }
                                            .show()
                                    }
                                }
                            }

                        hunterJobFavs.adapter = adapter
                    }

            }
            pbJhFav.visibility = View.GONE
            lyJobFavs.visibility = View.VISIBLE
        }
    }
}