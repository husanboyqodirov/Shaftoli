package softromeda.shaftoli

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HunterApplicationsAdapter(var mCtx: Context, var resources: Int, var items: MutableList<HunterApplicationsModel>):
    ArrayAdapter<HunterApplicationsModel>(mCtx, resources, items) {
    @SuppressLint("ViewHolder", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resources, null)

        val titleText: TextView = view.findViewById(R.id.txtHunNotTitle)
        val recText: TextView = view.findViewById(R.id.txtHunNotRec)
        val dateText: TextView = view.findViewById(R.id.txtHunNotDate)
        val statusText: TextView = view.findViewById(R.id.txtHunNotStatus)

        val mItem: HunterApplicationsModel = items[position]
        titleText.text = mItem.title
        recText.text = mItem.recName
        dateText.text = mItem.date
        statusText.text = mItem.status

        if(mItem.status == "Accepted!") {
            statusText.setTextColor(Color.parseColor("#00cc00"))
        } else if(mItem.status == "Rejected.") {
            statusText.setTextColor(Color.parseColor("#ff3300"))
        }

        return view
    }
}