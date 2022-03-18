package net.medlinker.imbusiness.ui.repository

import android.annotation.SuppressLint
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.medlinker.baseapp.entity.DataListResponse
import com.medlinker.network.retrofit.error.ErrorConsumer
import io.reactivex.disposables.Disposable
import io.realm.Realm
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMNetCallback
import net.medlinker.base.mvvm.extend.startRequest
import net.medlinker.base.network.HttpResultFunc
import net.medlinker.im.im.ImMsgRecivedManager
import net.medlinker.im.realm.MsgSessionDbEntity
import net.medlinker.im.util.SchedulersCompat
import net.medlinker.imbusiness.entity.ClinicMemberEntity
import net.medlinker.imbusiness.entity.ImButtonEntity
import net.medlinker.imbusiness.entity.ListStringEntity
import net.medlinker.imbusiness.network.Api
import net.medlinker.imbusiness.network.ApiManager
import net.medlinker.libhttp.host.HostManager
import java.util.*


/**
 * @author hmy
 * @time 12/6/21 13:45
 */
class ChatSessionRepository {

    fun getImButtonList(
        doctorId: Long,
        liveData: MutableLiveData<ArrayList<ImButtonEntity>>?,
        vm: BaseViewModel?
    ) {
        HostManager.getApi(Api::class.java)
            .getImButtons(doctorId)
            .startRequest(object : VMNetCallback<DataListResponse<ImButtonEntity>>(vm) {
                override fun onSuccess(data: DataListResponse<ImButtonEntity>) {
                    liveData?.value = data.list as ArrayList<ImButtonEntity>?
                }

            })
    }

    @SuppressLint("CheckResult")
    fun getUserList(groupId: Long, liveData: MutableLiveData<ClinicMemberEntity>?) {
        ApiManager.getClinicApi().getUserList(groupId)
            .compose(SchedulersCompat.applyIoSchedulers())
            .map(HttpResultFunc())
            .subscribe({
                liveData?.value = it
            }, ErrorConsumer())
    }

    private var mGetHistoryMsgDisposable: Disposable? = null

    fun getHistoryMsg(
        mSession: MsgSessionDbEntity?, limit: Int,
        liveData: MutableLiveData<ListStringEntity?>?
    ): Boolean {
        if (mGetHistoryMsgDisposable != null && !mGetHistoryMsgDisposable!!.isDisposed || null == mSession || !mSession.isValid
        ) {
            liveData!!.value = null
            return false
        }
        val observable = if (mSession.isGroup) ApiManager.getMsgApi().getGroupHistoryMsg(
            mSession.fromGroup.id, 1, limit,
            mSession.start
        ) else ApiManager.getMsgApi().getSingleChatHistroyMsg(
            mSession.fromUser.id, 1,
            mSession.fromUser.refrence.toLong(), 15, mSession.start
        )
        mGetHistoryMsgDisposable = observable
            .compose(SchedulersCompat.applyIoSchedulers())
            .map(HttpResultFunc())
            .filter { listStringEntity -> listStringEntity.list != null }
            .doOnNext { listStringEntity ->
                Realm.getDefaultInstance().use { realm ->
                    realm.executeTransaction { realm ->
                        for (s in listStringEntity.list) {
                            if (s == null) {
                                continue
                            }
                            val decode =
                                Base64.decode(s, Base64.NO_WRAP)
                            ImMsgRecivedManager.INSTANCE.saveHistoryMsg(decode, realm)
                        }
                    }
                }
            }
            .subscribe({ s -> liveData?.setValue(s) }, object : ErrorConsumer() {
                override fun accept(throwable: Throwable) {
                    super.accept(throwable)
                    liveData?.value = null
                }
            })
        return true
    }
}