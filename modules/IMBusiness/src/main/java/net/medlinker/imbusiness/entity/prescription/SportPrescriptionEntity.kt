package net.medlinker.imbusiness.entity.prescription

import io.realm.internal.Keep


/**
 * @author hmy
 * @time 12/17/21 11:12
 */
@Keep
data class SportPrescriptionEntity(val prescription: SportPrescriptBean?)

@Keep
data class SportPrescriptBean(
    val Entity: PrescriptEntity?,
    val diagnose: String?,
    val prTransNo: String?,
    val rehabilitationTrainingCycle: String?,
    val content: String?,
    val prescription: SportPrescription?
)

@Keep
data class PrescriptEntity(
    val sports: SportsBean?
)

@Keep
data class SportsBean(
    val surgery: SurgeryBean
)

@Keep
data class SurgeryBean(
    val id: Int,
    val name: String?,
    val time: Long
)

@Keep
data class SportPrescription(val statusEntity: SportStatusEntity?)

@Keep
data class SportStatusEntity(val prStatus: Int)