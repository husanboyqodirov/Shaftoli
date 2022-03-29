package softromeda.shaftoli

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_rec_profile.view.*

class RecProfileFrag : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_rec_profile, container, false)

        view.txtLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val userType = context?.getSharedPreferences("UserType", Context.MODE_PRIVATE)?.edit()
            userType?.putString("Type", "")
            userType?.apply()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        return view
    }

}