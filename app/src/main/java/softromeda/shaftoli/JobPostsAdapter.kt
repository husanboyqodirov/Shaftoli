package softromeda.shaftoli

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobPostsAdapter(var jobsList: ArrayList<Model>) :
    RecyclerView.Adapter<JobPostsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    interface ItemClick
    {
        fun onClick(view: View, position: Int)
    }

    interface ItemLongClick{
        fun onLongClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null
    var itemLongLick: ItemLongClick? = null


    override fun getItemCount(): Int {
        return jobsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jobsModel = jobsList[position]
        holder.bind(jobsModel)

        if(itemClick != null)
        {
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }

        if(itemLongLick != null)
        {
            holder.itemView.setOnLongClickListener { v ->
                itemLongLick?.onLongClick(v, position)
                true
            }
        }
    }

    class ViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_posts, viewGroup, false)) {

        fun bind(jobPostsModel: Model) {
            val jobTitle = itemView.findViewById<TextView>(R.id.txtTitle)
            val imgStar = itemView.findViewById<ImageView>(R.id.imgStar)
            val jobCompany = itemView.findViewById<TextView>(R.id.txtCorporate)
            val jobLocation = itemView.findViewById<TextView>(R.id.txtLocation)
            val jobSalary = itemView.findViewById<TextView>(R.id.txtSalary)
            val jobTime = itemView.findViewById<TextView>(R.id.txtWorkingTime)
            val jobEducation = itemView.findViewById<TextView>(R.id.txtEducation)
            val jobCategory = itemView.findViewById<TextView>(R.id.txtCategories)
            val jobCatLy = itemView.findViewById<LinearLayout>(R.id.lyJobCat)
            val jobCreated = itemView.findViewById<TextView>(R.id.txtJobTimestamp)


            jobTitle.text = jobPostsModel.jobsTitle
            jobCompany.text = jobPostsModel.jobsCompany
            jobLocation.text = jobPostsModel.jobsLocation
            jobSalary.text = jobPostsModel.jobsSalary
            jobTime.text = jobPostsModel.jobsTime
            jobEducation.text = jobPostsModel.jobsEducation
            jobCategory.text = jobPostsModel.jobCategories
            jobCreated.text = jobPostsModel.jobCreated

            if(jobPostsModel.viewID == "favs"){
                imgStar.visibility = View.VISIBLE
                jobCatLy.visibility = View.GONE
            }
        }

    }


}