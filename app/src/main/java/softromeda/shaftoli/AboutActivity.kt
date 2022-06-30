package softromeda.shaftoli

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val text = "<font color='#ff9999'>SH</font><font color='#00ffff'>AF</font><font color='green'>TO</font><font color='yellow'>LI</font>"

        txtAppTitle.setText(
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY),
            TextView.BufferType.SPANNABLE
        )
    }
}