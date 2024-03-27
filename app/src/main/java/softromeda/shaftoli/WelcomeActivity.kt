package softromeda.shaftoli

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import softromeda.shaftoli.databinding.WelcomeSliderBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: WelcomeSliderBinding
    private lateinit var myAdapter: MyAdapter
    private lateinit var dotsTv: Array<TextView?>
    private lateinit var layouts: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeSliderBinding.inflate(layoutInflater)
        val user = Firebase.auth.currentUser
        if (user != null) {
            val sharedPreferences = getSharedPreferences("shaftoli", Activity.MODE_PRIVATE)
            if (sharedPreferences.getString("userType", "") == "job_hunter")
                startActivity(Intent(this, JobHunterActivity::class.java))
            else
                startActivity(Intent(this, RecruiterActivity::class.java))
            finish()
            return
        }
        if (!isFirstTimeAppStart()) {
            setAppStartStatus(false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        statusBarTransparent()
        binding.btnNext.setOnClickListener {
            val currentPage: Int = binding.viewPager.currentItem + 1
            if (currentPage < layouts.size) {
                binding.viewPager.currentItem = currentPage
            } else {
                setAppStartStatus(false)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        binding.btnSkip.setOnClickListener {
            setAppStartStatus(false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        layouts = intArrayOf(R.layout.slide_1, R.layout.slide_2, R.layout.slide_3, R.layout.slide_4)
        myAdapter = MyAdapter(layouts, applicationContext)
        binding.viewPager.adapter = myAdapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == layouts.size - 1) {
                    binding.btnNext.text = "Start"
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnNext.text = "Next"
                    binding.btnSkip.visibility = View.VISIBLE
                }
                setDots(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        setDots(0)
    }

    private fun isFirstTimeAppStart(): Boolean {
        val pref = applicationContext.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)
        return pref.getBoolean("APP_START", true)
    }

    private fun setAppStartStatus(status: Boolean) {
        val pref = applicationContext.getSharedPreferences("shaftoli", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("APP_START", status)
        editor.apply()
    }

    private fun statusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val windows = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setDots(page: Int) {
        binding.dotsLayout.removeAllViews()
        dotsTv = arrayOfNulls(layouts.size)
        for (i in dotsTv.indices) {
            dotsTv[i] = TextView(this)
            dotsTv[i]!!.text = Html.fromHtml("&#8226;")
            dotsTv[i]!!.textSize = 50f
            dotsTv[i]!!.setTextColor(Color.parseColor("#a9b4bb"))
            binding.dotsLayout.addView(dotsTv[i])
        }
        if (dotsTv.isNotEmpty()) {
            dotsTv[page]!!.setTextColor(Color.parseColor("#3366ff"))
        }
    }
}