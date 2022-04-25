package softromeda.shaftoli

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.fragment_rec_vacancy.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class CategoryActivity : AppCompatActivity() {
    val list = mutableListOf<String>()
    var checked = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_category)

        val categories: MutableList<String> = ArrayList()

        try {
            val inputStream: InputStream = this.resources.openRawResource(R.raw.job_categories)
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

        val adapter = ArrayAdapter(this, R.layout.list_item, categories)
        (txtCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        txtChosenCategory.onItemClickListener =
            OnItemClickListener { parent, view, position, rowId ->
                try {
                    var inputStream: InputStream =
                        this.resources.openRawResource(R.raw.job_categories)
                    inputStream = when(position) {
                        0 ->  this.resources.openRawResource(R.raw.it_computers_internet)
                        1 ->  this.resources.openRawResource(R.raw.accounting_audit)
                        2 ->  this.resources.openRawResource(R.raw.design_photo)
                        3 ->  this.resources.openRawResource(R.raw.engineering_technology)
                        4 ->  this.resources.openRawResource(R.raw.human_resources)
                        5 ->  this.resources.openRawResource(R.raw.culture_arts)
                        6 ->  this.resources.openRawResource(R.raw.logistics_customs)
                        7 ->  this.resources.openRawResource(R.raw.marketing_advertising)
                        8 ->  this.resources.openRawResource(R.raw.medicine_pharmaceuticals)


                        else -> {
                            this.resources.openRawResource(R.raw.jurisprudence_notary)
                        }
                    }
                    val inputStreamReader = InputStreamReader(inputStream)
                    var line: String?
                    val br = BufferedReader(inputStreamReader)
                    line = br.readLine()
                    chipGroup.removeAllViews()
                    while (line != null) {
                        chipGroup.addChip(this, line!!)
                        line = br.readLine()
                    }
                    br.close()
                    catGIF.visibility = View.GONE
                    lyChipGroup.visibility = View.VISIBLE
                } catch (e: Exception) {
                }
            }
        btnCatDone.setOnClickListener {
            checked = ""
            for(i in 0 until list.size) {
                checked += if(i < list.size-1)
                    list[i] + ", "
                else
                    list[i]
            }
            val userType = getSharedPreferences(
                "shaftoli",
                Context.MODE_PRIVATE
            ).edit()
            userType.putString("chosenCats", checked)
            userType.putString("myField", txtChosenCategory.text.toString())
            userType.apply()
            finish()
            Toast.makeText(this, "Category's chosen.", Toast.LENGTH_SHORT).show()
        }
    }

    fun ChipGroup.addChip(context: Context, label: String) {
        Chip(context).apply {
            id = View.generateViewId()
            text = label
            isClickable = true
            isCheckable = true
            isCheckedIconVisible = true
            isFocusable = true
            setOnClickListener {
                if(isChecked) {
                    list.add(text as String)
                } else {
                    list.remove(text as String)
                }
            }
            addView(this)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}