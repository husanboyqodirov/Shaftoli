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
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.FragmentRecProfileBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class RecProfileFrag : Fragment() {
    private lateinit var binding: FragmentRecProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        val db = Firebase.firestore

        binding.txtProWebsite.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.txtLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val userType = context?.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)?.edit()
            userType?.putString("userType", "")
            userType?.apply()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        getProfileInfo()


        binding.btnProfileEdit.setOnClickListener {
            val dialogView: View = View.inflate(context, R.layout.rec_profile_edit, null)
            val dlg = AlertDialog.Builder(context)

            val docRef = Firebase.auth.currentUser?.let { it1 ->
                db.collection("recruiters").document(
                    it1.uid
                )
            }

            val txtEditName = dialogView.findViewById<EditText>(R.id.txtEditName)
            val txtEditAddress = dialogView.findViewById<EditText>(R.id.txtEditAddress)
            val txtEditField = dialogView.findViewById<AutoCompleteTextView>(R.id.txtEditField)
            val txtEditEmail = dialogView.findViewById<EditText>(R.id.txtEditEmail)
            val txtEditPhone = dialogView.findViewById<EditText>(R.id.txtEditPhone)
            val txtEditWebsite = dialogView.findViewById<EditText>(R.id.txtEditWebsite)
            val txtEditEmployees = dialogView.findViewById<EditText>(R.id.txtEditEmployees)
            val txtEditSelfIntro = dialogView.findViewById<EditText>(R.id.txtEditSelfIntro)

            docRef?.get()?.addOnSuccessListener { document ->
                txtEditName.setText(document.data?.get("name") as String)
                txtEditAddress.setText(document.data?.get("address") as String)
                txtEditField.setText(document.data?.get("field") as String)
                txtEditEmail.setText(document.data?.get("email") as String)
                txtEditPhone.setText(document.data?.get("phone") as String)
                txtEditWebsite.setText(document.data?.get("website") as String)
                txtEditEmployees.setText(document.data?.get("employees") as String)
                txtEditSelfIntro.setText(document.data?.get("self_intro") as String)
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
                txtEditField.setAdapter(adapter)

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
                                    "name" to txtEditName.text.toString(),
                                    "address" to txtEditAddress.text.toString(),
                                    "field" to txtEditField.text.toString(),
                                    "email" to txtEditEmail.text.toString(),
                                    "phone" to txtEditPhone.text.toString(),
                                    "website" to txtEditWebsite.text.toString(),
                                    "employees" to txtEditEmployees.text.toString(),
                                    "self_intro" to txtEditSelfIntro.text.toString()
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

        binding.btnAboutHunter.setOnClickListener {
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
            binding.txtProName.text = document.data?.get("name") as String
            binding.txtProAddress.text = document.data?.get("address") as String
            binding.txtProField.text = document.data?.get("field") as String
            binding.txtProEmail.text = document.data?.get("email") as String
            binding.txtProPhone.text = document.data?.get("phone") as String
            binding.txtProWebsite.text = document.data?.get("website") as String
            binding.txtProEmployees.text = document.data?.get("employees") as String
            binding.txtProSelfIntro.text = document.data?.get("self_intro") as String
        }
    }
}