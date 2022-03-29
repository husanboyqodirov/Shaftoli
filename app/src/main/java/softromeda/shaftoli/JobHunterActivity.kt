package softromeda.shaftoli

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*

class JobHunterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        bottom_nav_view.inflateMenu(R.menu.nav_menu_hunter)

        val hunterJobsFrag = HunterJobsFrag()
        val hunterSearchFrag = HunterSearchFrag()
        val hunterMessagesFrag = HunterMessagesFrag()
        val hunterNotificationsFrag = HunterNotificationsFrag()
        val hunterProfileFrag = HunterProfileFrag()


        bottom_nav_view.setOnNavigationItemSelectedListener {
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