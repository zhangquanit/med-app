package net.medlinker.imbusiness.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.medlinker.base.widget.badgeview.BadgeView
import com.medlinker.lib.utils.MedTimeUtil
import net.medlinker.base.adapter.BaseSimpleListAdapter
import net.medlinker.imbusiness.R
import net.medlinker.imbusiness.entity.doctor.DoctorConversationEntity


/**
 * @author hmy
 * @time 11/29/21 17:30
 */
class DoctorConversationAdapter(private val list: ArrayList<DoctorConversationEntity>) :
    RecyclerView.Adapter<DoctorConversationAdapter.DoctorVH>() {

    private var mItemClickListener: BaseSimpleListAdapter.OnItemClickListener<DoctorConversationEntity>? =
        null

    class DoctorVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headIv: ImageView = itemView.findViewById(R.id.iv_head)
        val badgeView: BadgeView = itemView.findViewById(R.id.bv_unread)
        val userNameTv: TextView = itemView.findViewById(R.id.tv_user_name)
        val jobTitleTv: TextView = itemView.findViewById(R.id.tv_job_title)
        val timeTv: TextView = itemView.findViewById(R.id.tv_time)
        val orderStatusTv: TextView = itemView.findViewById(R.id.tv_order_status)
        val hospitalInfoTv: TextView = itemView.findViewById(R.id.tv_hospital_info)
        val msgTv: TextView = itemView.findViewById(R.id.tv_msg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorVH {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doctor_conversation, parent, false)
        return DoctorVH(itemView)
    }

    override fun onBindViewHolder(holder: DoctorVH, position: Int) {
        val entity = list[position]
        Glide.with(holder.itemView.context).load(entity.avatar)
            .apply(
                RequestOptions().transform(CircleCrop())
                    .placeholder(R.drawable.ic_patient_default)
                    .error(R.drawable.ic_patient_default)
            )
            .into(holder.headIv)
        holder.badgeView.badgeNumber = entity.unreadMsgCount
        holder.userNameTv.text = entity.name
        holder.jobTitleTv.text = entity.title
        holder.timeTv.text = MedTimeUtil.formatOrderTime(entity.updatedTime)
        holder.hospitalInfoTv.text = "${entity.hospitalName} | ${entity.sectionName}"
        holder.msgTv.text = entity.msg
        holder.orderStatusTv.visibility = if (entity.servicePackUsing) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(position, entity, it)
        }
    }

    fun setOnItemClickListener(itemClickListener: BaseSimpleListAdapter.OnItemClickListener<DoctorConversationEntity>?) {
        mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }
}