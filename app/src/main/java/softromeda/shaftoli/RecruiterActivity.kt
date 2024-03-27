package softromeda.shaftoli

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import softromeda.shaftoli.databinding.ActivityBottomNavBinding

class RecruiterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.bottomNavView.inflateMenu(R.menu.nav_menu_recruiter)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;

        val recJobPostFrag = RecJobPostFrag()
        val recVacancyFrag = RecVacancyFrag()
        val recNotificationsFrag = RecNotificationsFrag()
        val recProfileFrag = RecProfileFrag()

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navRecJobPosts -> setCurrentFragment(recJobPostFrag)
                R.id.navRecVacancy -> setCurrentFragment(recVacancyFrag)
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