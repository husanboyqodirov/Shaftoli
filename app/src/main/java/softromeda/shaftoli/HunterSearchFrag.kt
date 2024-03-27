package softromeda.shaftoli

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.FragmentHunterSearchBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class HunterSearchFrag : Fragment() {
    private lateinit var binding: FragmentHunterSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHunterSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        val categories: MutableList<String> = ArrayList()

        try {
            val inputStream: InputStream = this.resources.openRawResource(R.raw.job_categories)
            val inputStreamReader = InputStreamReader(inputStream)
            var line: String?
            val br = BufferedReader(inputStreamReader)
            line = br.readLine()
            while (line != null) {
                categories.add(line)
                line = br.readLine()
            }
            br.close()
        } catch (e: Exception) {
        }

        var adapter = context?.let { ArrayAdapter(it, R.layout.list_item, categories) }
        (binding.txtCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        val levels = listOf(
            "Associate",
            "Bachelor's",
            "Master's",
            "Doctoral",
            "Not needed"
        )
        adapter = ArrayAdapter(requireContext(), R.layout.list_item, levels)
        (binding.txtEdu.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.btnSearch.setOnClickListener {
            getVacancies(
                binding.txtChosenCategory.text.toString(),
                binding.txtChosenEdu.text.toString(),
                binding.txtSalaryMax.text.toString(),
                binding.txtSalaryMin.text.toString(),
                binding.txtSearchQuery.text.toString()
            )

        }

        return view
    }

    private fun getVacancies(
        txtCat: String,
        txtEdu: String,
        txtSalaryMax: String,
        txtSalaryMin: String,
        txtQuery: String
    ) {
        binding.pbJobSearch.visibility = View.VISIBLE
        var favs = ""
        val jobIDs = mutableListOf<String>()
        val db = Firebase.firestore
        db.collection("vacancies")
            .get()
            .addOnSuccessListener { result ->
                val precautionsList = ArrayList<Model>()
                for (document in result) {
                    jobIDs.add(document.id)
                    if (txtSalaryMin.toFloat() <= (document.data["salary"] as String).toFloat()) {
                        addJob(precautionsList, document)
                    }
                }
                val precautionsAdapter = JobPostsAdapter(precautionsList)
                binding.hunterJobPosts.adapter = precautionsAdapter

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
                binding.hunterJobPosts.adapter = adapter
                binding.pbJobSearch.visibility = View.GONE
                binding.hunterJobPosts.visibility = View.VISIBLE
            }
    }
}

fun addJob(precautionsList: ArrayList<Model>, document: QueryDocumentSnapshot) {
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