package softromeda.shaftoli

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hunter_jobs.*
import kotlinx.android.synthetic.main.fragment_hunter_jobs.view.*


class HunterJobsFrag : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hunter_jobs, container, false)
        view.hunterJobPosts.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )

        var ascending = false
        var sortBy = "created"

        getVacancies(true, "created", Query.Direction.DESCENDING)
        view.btnSortJobType.setOnClickListener {
            val listPopupWindow =
                context?.let { it1 -> ListPopupWindow(it1, null, R.attr.listPopupWindowStyle) }
            if (listPopupWindow != null) {
                listPopupWindow.anchorView = btnSortJobType
            }
            val items = listOf("All Fields", "Speciality")
            val adapter = ArrayAdapter(requireContext(), R.layout.popup_menu, items)
            listPopupWindow?.setAdapter(adapter)

            listPopupWindow?.setOnItemClickListener { _: AdapterView<*>?, _: View?, chosen: Int, _: Long ->
                listPopupWindow.dismiss()
                view.btnSortJobType.text = items[chosen]
                if (chosen == 0) {
                    getVacancies(true, sortBy, Query.Direction.DESCENDING)
                    btnSortReverse.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                } else if (chosen == 1) {
                    sortBy = "salary"
                    getVacancies(false, sortBy, Query.Direction.DESCENDING)
                    btnSortReverse.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                }
                ascending = false
            }
            listPopupWindow?.show()
        }

        view.btnSort.setOnClickListener {
            val listPopupWindow =
                context?.let { it1 -> ListPopupWindow(it1, null, R.attr.listPopupWindowStyle) }
            if (listPopupWindow != null) {
                listPopupWindow.anchorView = btnSort
            }
            val items = listOf("Date", "Salary")
            val adapter = ArrayAdapter(requireContext(), R.layout.popup_menu, items)
            listPopupWindow?.setAdapter(adapter)

            listPopupWindow?.setOnItemClickListener { _: AdapterView<*>?, _: View?, chosen: Int, _: Long ->
                listPopupWindow.dismiss()
                view.btnSort.text = items[chosen]
                if (chosen == 0) {
                    sortBy = "created"
                    getVacancies(true, sortBy, Query.Direction.DESCENDING)
                    btnSortReverse.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                } else if (chosen == 1) {
                    sortBy = "salary"
                    getVacancies(true, sortBy, Query.Direction.DESCENDING)
                    btnSortReverse.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                }
                ascending = false
            }
            listPopupWindow?.show()
        }

        view.btnSortReverse.setOnClickListener {
            if (ascending) {
                getVacancies(true, sortBy, Query.Direction.DESCENDING)
                btnSortReverse.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_arrow_up,
                    0,
                    0,
                    0
                )
            } else {
                getVacancies(true, sortBy, Query.Direction.ASCENDING)
                btnSortReverse.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_arrow_down,
                    0,
                    0,
                    0
                )
            }
            ascending = !ascending
        }
        return view
    }

    fun getVacancies(anyField: Boolean, sortTag: String, direct: Query.Direction) {
        val myFieldShared = context?.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)
        val myField = myFieldShared?.getString("myField", "")
        var favs = ""
        val jobIDs = mutableListOf<String>()
        val db = Firebase.firestore
        db.collection("vacancies")
            .orderBy(sortTag, direct)
            .get()
            .addOnSuccessListener { result ->
                val precautionsList = ArrayList<Model>()
                for (document in result) {
                    if (anyField) {
                        jobIDs.add(document.id)
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
                    } else if (document.data["field"] == myField) {
                        jobIDs.add(document.id)
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
                hunterJobPosts.adapter = precautionsAdapter

                val adapter = JobPostsAdapter(precautionsList)
                adapter.itemClick = object : JobPostsAdapter.ItemClick {
                    override fun onClick(view: View, position: Int) {
                        val intent = Intent(context, JobViewActivity::class.java)
                        intent.putExtra("jobID", jobIDs[position])
                        startActivity(intent)
                        enterTransition
                    }
                }

                adapter.itemLongLick = object : JobPostsAdapter.ItemLongClick {
                    override fun onLongClick(view: View, position: Int) {
                        context?.let {
                            MaterialAlertDialogBuilder(
                                it,
                                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
                            )
                                .setMessage("Save this vacancy to favorites?")
                                .setNegativeButton("No") { _, _ ->
                                }
                                .setPositiveButton("Yes") { _, _ ->
                                    val docRef = Firebase.auth.currentUser?.let { it1 ->
                                        db.collection("job_hunters").document(
                                            it1.uid
                                        )
                                    }
                                    docRef?.get()?.addOnSuccessListener { document ->
                                        favs = document.data?.get("favorites") as String
                                        Firebase.auth.currentUser?.let { it1 ->
                                            db.collection("job_hunters").document(it1.uid)
                                                .set(
                                                    hashMapOf("favorites" to "${jobIDs[position]}$favs"),
                                                    SetOptions.merge()
                                                )
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Vacancy saved to your favorites.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }
                                }
                                .show()
                        }
                    }
                }

                hunterJobPosts.adapter = adapter

                pbJobPosts.visibility = View.GONE
                lyJobPosts.visibility = View.VISIBLE
            }
            .addOnFailureListener {
            }
    }
}