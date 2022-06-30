package softromeda.shaftoli

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_rec_profile.*
import kotlinx.android.synthetic.main.fragment_rec_profile.view.*
import kotlinx.android.synthetic.main.hunter_profile_edit.view.txtEditAddress
import kotlinx.android.synthetic.main.hunter_profile_edit.view.txtEditEmail
import kotlinx.android.synthetic.main.hunter_profile_edit.view.txtEditField
import kotlinx.android.synthetic.main.hunter_profile_edit.view.txtEditName
import kotlinx.android.synthetic.main.hunter_profile_edit.view.txtEditPhone
import kotlinx.android.synthetic.main.hunter_profile_edit.view.txtEditSelfIntro
import kotlinx.android.synthetic.main.rec_profile_edit.view.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class RecProfileFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_rec_profile, container, false)
        val db = Firebase.firestore

        view.txtProWebsite.paintFlags = Paint.UNDERLINE_TEXT_FLAG

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
            val dialogView: View = View.inflate(context, R.layout.rec_profile_edit, null)
            val dlg = AlertDialog.Builder(context)

            val docRef = Firebase.auth.currentUser?.let { it1 ->
                db.collection("recruiters").document(
                    it1.uid
                )
            }
            docRef?.get()?.addOnSuccessListener { document ->
                dialogView.txtEditName.setText(document.data?.get("name") as String)
                dialogView.txtEditAddress.setText(document.data?.get("address") as String)
                dialogView.txtEditField.setText(document.data?.get("field") as String)
                dialogView.txtEditEmail.setText(document.data?.get("email") as String)
                dialogView.txtEditPhone.setText(document.data?.get("phone") as String)
                dialogView.txtEditWebsite.setText(document.data?.get("website") as String)
                dialogView.txtEditEmployees.setText(document.data?.get("employees") as String)
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
                    Firebase.auth.currentUser?.let { it1 ->
                        db.collection("recruiters").document(it1.uid)
                            .set(
                                hashMapOf(
                                    "name" to dialogView.txtEditName.text.toString(),
                                    "address" to dialogView.txtEditAddress.text.toString(),
                                    "field" to dialogView.txtEditField.text.toString(),
                                    "email" to dialogView.txtEditEmail.text.toString(),
                                    "phone" to dialogView.txtEditPhone.text.toString(),
                                    "website" to dialogView.txtEditWebsite.text.toString(),
                                    "employees" to dialogView.txtEditEmployees.text.toString(),
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
            Firebase.firestore.collection("recruiters").document(
                it1.uid
            )
        }
        docRef?.get()?.addOnSuccessListener { document ->
            txtProName.text = document.data?.get("name") as String
            txtProAddress.text = document.data?.get("address") as String
            txtProField.text = document.data?.get("field") as String
            txtProEmail.text = document.data?.get("email") as String
            txtProPhone.text = document.data?.get("phone") as String
            txtProWebsite.text = document.data?.get("website") as String
            txtProEmployees.text = document.data?.get("employees") as String
            txtProSelfIntro.text = document.data?.get("self_intro") as String
        }
    }
}