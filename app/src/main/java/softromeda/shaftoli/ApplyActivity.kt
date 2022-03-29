package softromeda.shaftoli

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_apply.*

class ApplyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_apply)

        chkExperience.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                lyExperience.visibility = View.VISIBLE
            else
                lyExperience.visibility = View.GONE
        }

        slYears.addOnChangeListener { slider, value, fromUser ->
            txtYears.text = slYears.value.toInt().toString() + " years"
        }

        btnSubmit.setOnClickListener {
            MaterialAlertDialogBuilder(
                this,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
            )
            .setTitle("Please confirm")
            .setMessage("Are you sure you want to send your application to recruiter for this vacancy?")
            .setNegativeButton("No Cancel") { _, _ ->
            }
            .setPositiveButton("Yes Submit") { _, _ ->
                Snackbar.make(
                    snackViewApply,
                    "You have successfully applied for this job! Recruiter will contact you soon.",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("OK") {}.show()
            }
            .show()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}