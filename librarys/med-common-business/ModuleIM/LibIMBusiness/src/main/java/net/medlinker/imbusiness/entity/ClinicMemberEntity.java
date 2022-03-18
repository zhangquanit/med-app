package net.medlinker.imbusiness.entity;


import net.medlinker.base.entity.DataEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hmy
 * @time 2020/7/14 14:59
 */
public class ClinicMemberEntity extends DataEntity {
    private ImUserInfoEntity doctor;
    private ImUserInfoEntity patient;
    private ImUserInfoEntity assistant;
    private List<ImUserInfoEntity> healthManager;
    private ImUserInfoEntity nutritionist;
    private ImUserInfoEntity psychotherapist;

    private List<ImUserInfoEntity> allMemberList = new ArrayList<>();

    public List<ImUserInfoEntity> getAllMemberList() {
        allMemberList.clear();
        if (doctor != null) {
            doctor.setUserType("doctor");
            allMemberList.add(doctor);
        }
        if (assistant != null) {
            assistant.setUserType("assistant");
            allMemberList.add(assistant);
        }
        if (healthManager != null) {
            for (ImUserInfoEntity infoEntity : healthManager) {
                infoEntity.setUserType("healthManager");
                allMemberList.add(infoEntity);
            }
        }
        if (nutritionist != null) {
            nutritionist.setUserType("nutritionist");
            allMemberList.add(nutritionist);
        }
        if (psychotherapist != null) {
            psychotherapist.setUserType("psychotherapist");
            allMemberList.add(psychotherapist);
        }

        return allMemberList;
    }
}
