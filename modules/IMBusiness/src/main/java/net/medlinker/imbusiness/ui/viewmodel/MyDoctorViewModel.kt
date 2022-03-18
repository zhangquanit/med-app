package net.medlinker.imbusiness.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.medlinker.baseapp.entity.DataListResponse
import com.medlinker.router.MedRouterHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMNetCallback
import net.medlinker.base.mvvm.extend.startRequest
import net.medlinker.im.MsgConstants
import net.medlinker.im.helper.EntityConvertHelper
import net.medlinker.im.helper.MsgDbManager
import net.medlinker.im.realm.MsgSessionDbEntity
import net.medlinker.imbusiness.entity.ServicePackDoctorEntity
import net.medlinker.imbusiness.entity.doctor.DoctorConversationEntity
import net.medlinker.imbusiness.entity.intent.ChatSessionIntentData
import net.medlinker.imbusiness.network.Api
import net.medlinker.imbusiness.ui.ChatSessionActivity
import net.medlinker.libhttp.host.HostManager


/**
 * @author hmy
 * @time 11/30/21 19:56
 */
class MyDoctorViewModel : BaseViewModel() {

    val historySessionLiveData = MutableLiveData<Boolean>()
    val doctorListLiveData = MutableLiveData<ArrayList<DoctorConversationEntity>?>()
    val totalUnreadLiveData = MutableLiveData<Int>()
    private val mRealm = Realm.getDefaultInstance()
    private var mAllSessionData: RealmResults<MsgSessionDbEntity>? = null
    val doctorList: ArrayList<DoctorConversationEntity> = ArrayList()
    val servicePackList: ArrayList<ServicePackDoctorEntity> = ArrayList()
    private var mSortDisposable: Disposable? = null
    private var mTransDisposable: Disposable? = null

    fun loadData() {
        queryAllSessionData()
        getDoctorList()
    }

    private fun queryAllSessionData() {
        val fieldNames = arrayOf("timeStamp")
        val sortOrders = arrayOf(Sort.DESCENDING)
        mAllSessionData = mRealm.where(MsgSessionDbEntity::class.java)
                .equalTo("sessionType", MsgConstants.SESSION_TYPE_SUIZHEN)
                .sort(fieldNames, sortOrders).findAllAsync()

        mAllSessionData?.let {
            it.removeAllChangeListeners()
            it.addChangeListener(RealmChangeListener<RealmResults<MsgSessionDbEntity>> {
                sortList()
            })
        }
    }

    private fun sortList() {
        mSortDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }

        mSortDisposable = Observable.just(0)
                .observeOn(Schedulers.io())
                .map {
                    var totalUnreadCount = 0
                    Realm.getDefaultInstance().use { realm ->
                        for (entity in doctorList) {
                            val sessionId =
                                    EntityConvertHelper.getGroupSessionId(entity.groupId)
                            val sessionDbEntity = realm.where(
                                    MsgSessionDbEntity::class.java
                            )
                                    .equalTo("id", sessionId).findFirst()

                            if (sessionDbEntity != null) {
                                entity.updatedTime = sessionDbEntity.timeStamp
                                entity.msg = sessionDbEntity.content
                                entity.unreadMsgCount = sessionDbEntity.unreadMsgRealCount
                                totalUnreadCount += entity.unreadMsgCount
                            }
                        }
                    }
                    doctorList.sort()
                    totalUnreadCount
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    totalUnreadLiveData.value = it
                    doctorListLiveData.value = doctorList
                }
                ) { doctorListLiveData.value = doctorList }
    }

    fun getDoctorList() {
        HostManager.getApi(Api::class.java)
                .getDoctorList(1160)
                .startRequest(object : VMNetCallback<DataListResponse<DoctorConversationEntity>>(this) {
                    override fun onSuccess(data: DataListResponse<DoctorConversationEntity>) {
                        doctorList.clear()
                        data.list?.let {
                            if (it.size > 0) {
                                doctorList.addAll(it)
                            }
                        }
                        sortList()
                        getServicePackList()
                    }

                })
    }

    private fun getServicePackList() {
        AccountUtil.getLoginInfo()?.let { info ->
            HostManager.getApi(Api::class.java)
                    .getServicePackList(info.userId.toLong())
                    .startRequest(object :
                            VMNetCallback<DataListResponse<ServicePackDoctorEntity>>(this) {
                        override fun onSuccess(data: DataListResponse<ServicePackDoctorEntity>) {
                            servicePackList.clear()
                            data.list?.let {
                                if (it.size > 0) {
                                    servicePackList.addAll(it)
                                }
                            }

                            transServicePackConversation()
                        }

                    })
        }
    }

    private fun transServicePackConversation() {
        mTransDisposable?.dispose()
        mTransDisposable = Observable.just(0)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    for (servicePack in servicePackList) {
                        for (entity in doctorList) {
                            if (entity.doctorId == servicePack.doctorId) {
                                entity.servicePackUsing = true
                                if (entity.updatedTime == 0L) {
                                    entity.updatedTime = servicePack.buyTime
                                }
                                break
                            }
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sortList()
                }, { sortList() })

    }

    fun gotoChat(context: Context, entity: DoctorConversationEntity) {
        val sessionId = EntityConvertHelper.getGroupSessionId(entity.groupId)
        val sessionDbEntity =
                MsgDbManager.INSTANCE.sessionDbDao.getSessionById(sessionId, mRealm)
        if (sessionDbEntity == null) {
            val link =
                    "/message/group?groupId=${entity.groupId}&type=${MsgConstants.SESSION_TYPE_SUIZHEN}"
            MedRouterHelper.withUrl(link).queryTarget().navigation(context)
        } else {
            val isClearUnread = entity.unreadMsgCount != 0
            if (isClearUnread) {
                mRealm.executeTransaction {
                    MsgDbManager.INSTANCE.sessionDbDao.clearUnreadCount(sessionDbEntity, mRealm)
                }
            }

            val intent = Intent(context, ChatSessionActivity::class.java)
            intent.putExtra(ChatSessionActivity.DATA_KEY, ChatSessionIntentData(sessionId))
            context.startActivity(intent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (mRealm != null && !mRealm.isClosed) {
            mRealm.removeAllChangeListeners()
            mRealm.close()
        }
    }
}
