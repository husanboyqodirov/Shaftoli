package softromeda.shaftoli

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*

class RecruiterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        bottom_nav_view.inflateMenu(R.menu.nav_menu_recruiter)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;

        val recJobPostFrag = RecJobPostFrag()
        val recApplicantsFrag = RecApplicantsFrag()
        val recMessagesFrag = RecMessagesFrag()
        val recNotificationsFrag = RecNotificationsFrag()
        val recProfileFrag = RecProfileFrag()

        bottom_nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navRecJobPosts -> setCurrentFragment(recJobPostFrag)
                R.id.navRecApplicants -> setCurrentFragment(recApplicantsFrag)
                R.id.navRecMessages -> setCurrentFragment(recMessagesFrag)
                R.id.navRecNotification -> setCurrentFragment(recNotificationsFrag)
                R.id.navRecProfile -> setCurrentFragment(recProfileFrag)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_fragment, fragment)
            commit()
        }
}