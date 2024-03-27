package softromeda.shaftoli

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.FragmentHunterProfileBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class HunterProfileFrag : Fragment() {
    private lateinit var binding: FragmentHunterProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentHunterProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        val db = Firebase.firestore

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
            val dialogView: View = View.inflate(context, R.layout.hunter_profile_edit, null)
            val dlg = AlertDialog.Builder(context)
            val txtEditname = dialogView.findViewById<EditText>(R.id.txtEditName)
            val btnMale = dialogView.findViewById<RadioButton>(R.id.btnMale)
            val btnFemale = dialogView.findViewById<RadioButton>(R.id.btnFemale)
            val txtEditAddress = dialogView.findViewById<EditText>(R.id.txtEditAddress)
            val txtEditEducation = dialogView.findViewById<EditText>(R.id.txtEditEducation)
            val txtEditField = dialogView.findViewById<AutoCompleteTextView>(R.id.txtEditField)
            val txtEditSkills = dialogView.findViewById<EditText>(R.id.txtEditSkills)
            val txtEditEmail = dialogView.findViewById<EditText>(R.id.txtEditEmail)
            val txtEditPhone = dialogView.findViewById<EditText>(R.id.txtEditPhone)
            val txtEditWorkHisotry = dialogView.findViewById<EditText>(R.id.txtEditWorkHisotry)
            val txtEditSelfIntro = dialogView.findViewById<EditText>(R.id.txtEditSelfIntro)

            val docRef = Firebase.auth.currentUser?.let { it1 ->
                db.collection("job_hunters").document(
                    it1.uid
                )
            }
            docRef?.get()?.addOnSuccessListener { document ->

                txtEditname.setText(document.data?.get("name") as String)
                if (document.data?.get("gender") as String == "Male")
                    btnMale.isChecked = true
                else
                    btnFemale.isChecked = true
                txtEditAddress.setText(document.data?.get("address") as String)
                txtEditEducation.setText(document.data?.get("education") as String)
                txtEditField.setText(document.data?.get("field") as String)
                txtEditSkills.setText(document.data?.get("skills") as String)
                txtEditEmail.setText(document.data?.get("email") as String)
                txtEditPhone.setText(document.data?.get("phone") as String)
                txtEditWorkHisotry.setText(document.data?.get("work_history") as String)
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
                    val myField = context?.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)
                        ?.edit()
                    myField?.putString("myField", txtEditField.text.toString())
                    myField?.apply()
                    val txtGender = if (btnMale.isChecked)
                        "Male"
                    else
                        "Female"
                    Firebase.auth.currentUser?.let { it1 ->
                        db.collection("job_hunters").document(it1.uid)
                            .set(
                                hashMapOf(
                                    "name" to txtEditname.text.toString(),
                                    "gender" to txtGender,
                                    "field" to txtEditField.text.toString(),
                                    "education" to txtEditEducation.text.toString(),
                                    "skills" to txtEditSkills.text.toString(),
                                    "email" to txtEditEmail.text.toString(),
                                    "phone" to txtEditPhone.text.toString(),
                                    "work_history" to txtEditWorkHisotry.text.toString(),
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
            Firebase.firestore.collection("job_hunters").document(
                it1.uid
            )
        }
        docRef?.get()?.addOnSuccessListener { document ->
            binding.txtProName.text = document.data?.get("name") as String
            binding.txtProGender.text = document.data?.get("gender") as String
            if (document.data?.get("gender") as String == "Male")
                binding.profileImage.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_male
                    )
                })
            else
                binding.profileImage.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.profile_female
                    )
                })
            binding.txtProAddress.text = document.data?.get("address") as String
            binding.txtProEducation.text = document.data?.get("education") as String
            binding.txtProSkills.text = document.data?.get("skills") as String
            binding.txtProField.text = document.data?.get("field") as String
            binding.txtProEmail.text = document.data?.get("email") as String
            binding.txtProPhone.text = document.data?.get("phone") as String
            binding.txtProWorkHistory.text = document.data?.get("work_history") as String
            binding.txtProSelfIntro.text = document.data?.get("self_intro") as String
        }
    }
}