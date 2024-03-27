package softromeda.shaftoli

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import softromeda.shaftoli.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "<font color='#ff9999'>SH</font><font color='#00ffff'>AF</font><font color='green'>TO</font><font color='yellow'>LI</font>"

        binding.txtAppTitle.setText(
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY),
            TextView.BufferType.SPANNABLE
        )
    }
}