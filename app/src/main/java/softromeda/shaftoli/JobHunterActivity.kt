package softromeda.shaftoli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import softromeda.shaftoli.databinding.ActivityBottomNavBinding

class JobHunterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.inflateMenu(R.menu.nav_menu_hunter)

        val hunterJobsFrag = HunterJobsFrag()
        val hunterSearchFrag = HunterSearchFrag()
        val hunterMessagesFrag = HunterFavoritesFrag()
        val hunterNotificationsFrag = HunterNotificationsFrag()
        val hunterProfileFrag = HunterProfileFrag()


        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHunterJobs -> setCurrentFragment(hunterJobsFrag)
                R.id.navHunterSearch -> setCurrentFragment(hunterSearchFrag)
                R.id.navHunterMessages -> setCurrentFragment(hunterMessagesFrag)
                R.id.navHunterNotification -> setCurrentFragment(hunterNotificationsFrag)
                R.id.navHunterProfile -> setCurrentFragment(hunterProfileFrag)
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