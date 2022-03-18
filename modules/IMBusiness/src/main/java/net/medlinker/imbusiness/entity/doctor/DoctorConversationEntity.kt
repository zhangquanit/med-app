package net.medlinker.imbusiness.entity.doctor

import androidx.annotation.Keep
import net.medlinker.base.entity.DataEntity


/**
 * @author hmy
 * @time 11/29/21 17:38
 */
@Keep
data class DoctorConversationEntity(
    val doctorId: Long,
    var avatar: String?,
    val name: String?,
    val title: String?,
    val hospitalName: String?,
    val sectionName: String?,
    var msg: String?,
    val groupId: Long = 0,
    var updatedTime: Long = 0,
    var unreadMsgCount: Int = 0,
    var servicePackUsing: Boolean = false
) : DataEntity(), Comparable<DoctorConversationEntity> {
    override fun compareTo(other: DoctorConversationEntity): Int {
        if (servicePackUsing != other.servicePackUsing) {
            if (servicePackUsing) {
                return -1
            }
            return 1
        }
        val time1 = updatedTime
        val time2: Long = other.updatedTime
        return time2.compareTo(time1)
    }
}
