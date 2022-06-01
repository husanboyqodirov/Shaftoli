package softromeda.shaftoli

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hunter_profile.*
import kotlinx.android.synthetic.main.fragment_hunter_profile.view.*
import kotlinx.android.synthetic.main.hunter_profile_edit.view.*
import kotlinx.android.synthetic.main.signup.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class HunterProfileFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_hunter_profile, container, false)
        val db = Firebase.firestore

        view.txtLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val userType = context?.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)?.edit()
            userType?.putString("userType", "")
            userType?.apply()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        getProfileInfo()


        view.btnProfileEdit.setOnClickListener {
            val dialogView: View = View.inflate(context, R.layout.hunter_profile_edit, null)
            val dlg = AlertDialog.Builder(context)

            val docRef = Firebase.auth.currentUser?.let { it1 ->
                db.collection("job_hunters").document(
                    it1.uid
                )
            }
            docRef?.get()?.addOnSuccessListener { document ->
                dialogView.txtEditName.setText(document.data?.get("name") as String)
                if (document.data?.get("gender") as String == "Male")
                    dialogView.btnMale.isChecked = true
                else
                    dialogView.btnFemale.isChecked = true
                dialogView.txtEditAddress.setText(document.data?.get("address") as String)
                dialogView.txtEditEducation.setText(document.data?.get("education") as String)
                dialogView.txtEditField.setText(document.data?.get("field") as String)
                dialogView.txtEditSkills.setText(document.data?.get("skills") as String)
                dialogView.txtEditEmail.setText(document.data?.get("email") as String)
                dialogView.txtEditPhone.setText(document.data?.get("phone") as String)
                dialogView.txtEditWorkHisotry.setText(document.data?.get("work_history") as String)
                dialogView.txtEditSelfIntro.setText(document.data?.get("self_intro") as String)
                val categories: MutableList<String> = ArrayList()

                try {
                    val inputStream: InputStream =
                        this.resources.openRawResource(R.raw.job_categories)
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

                val adapter = context?.let { it1 ->
                    ArrayAdapter(
                        it1,
                        R.layout.list_item,
                        categories
                    )
                }
                dialogView.txtEditField.setAdapter(adapter)

            }

            dlg.setTitle("Edit Your Profile")
            dlg.setIcon(R.drawable.ic_profile)
            dlg.setView(dialogView)
            dlg.setPositiveButton("Save") { dialog, which ->
                run {
                    val myField = context?.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)
                        ?.edit()
                    myField?.putString("myField", dialogView.txtEditField.text.toString())
                    myField?.apply()
                    val txtGender = if (dialogView.btnMale.isChecked)
                        "Male"
                    else
                        "Female"
                    Firebase.auth.currentUser?.let { it1 ->
                        db.collection("job_hunters").document(it1.uid)
                            .set(
                                hashMapOf(
                                    "name" to dialogView.txtEditName.text.toString(),
                                    "gender" to txtGender,
                                    "field" to dialogView.txtEditField.text.toString(),
                                    "education" to dialogView.txtEditEducation.text.toString(),
                                    "skills" to dialogView.txtEditSkills.text.toString(),
                                    "email" to dialogView.txtEditEmail.text.toString(),
                                    "phone" to dialogView.txtEditPhone.text.toString(),
                                    "work_history" to dialogView.txtEditWorkHisotry.text.toString(),
                                    "self_intro" to dialogView.txtEditSelfIntro.text.toString()
                                ),
                                SetOptions.merge()
                            )
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Profile updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
            dlg.setNegativeButton("Cancel", null)
            dlg.show()
        }

        view.btnAboutHunter.setOnClickListener {
            startActivity(Intent(context, AboutActivity::class.java))
        }

        return view
    }

    private fun getProfileInfo() {
        val docRef = Firebase.auth.currentUser?.let { it1 ->
            Firebase.firestore.collection("job_hunters").document(
                it1.uid
            )
        }
        docRef?.get()?.addOnSuccessListener { document ->
            txtProName.text = document.data?.get("name") as String
            txtProGender.text = document.data?.get("gender") as String
            if (document.data?.get("gender") as String == "Male")
                profile_image.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_male
                    )
                })
            else
                profile_image.setImageDrawable(context?.let {
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