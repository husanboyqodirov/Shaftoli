package softromeda.shaftoli

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class RecNotifyAdapter(
    var mCtx: Context,
    var resources: Int,
    var items: MutableList<RecNotifyModel>
) : ArrayAdapter<RecNotifyModel>(mCtx, resources, items) {
    @SuppressLint("ViewHolder", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resources, null)

        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        val titleText: TextView = view.findViewById(R.id.txtRecNotTitle)
        val applicantText: TextView = view.findViewById(R.id.txtRecNotApplicant)
        val dateText: TextView = view.findViewById(R.id.txtRecNotDate)

        val mItem: RecNotifyModel = items[position]
        if (mItem.applicantGender == "Male")
            imgProfile.setImageDrawable(mCtx.resources.getDrawable(R.drawable.profile_male))
        else
            imgProfile.setImageDrawable(mCtx.resources.getDrawable(R.drawable.profile_female))
        titleText.text = mItem.title
        applicantText.text = mItem.applicantName
        dateText.text = mItem.date

        return view
    }
}