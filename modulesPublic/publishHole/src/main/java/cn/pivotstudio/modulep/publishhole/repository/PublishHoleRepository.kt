package cn.pivotstudio.modulep.publishhole.repository

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.BaseObserver
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.NetworkApi
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler.ResponseThrowable
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.util.data.GetUrlUtil
import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse
import cn.pivotstudio.modulep.publishhole.model.ForestListsResponse
import cn.pivotstudio.modulep.publishhole.model.ForestTypeResponse
import cn.pivotstudio.modulep.publishhole.model.MsgResponse
import cn.pivotstudio.modulep.publishhole.network.PHRequestInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * @classname:PublishHoleRepository
 * @description:
 * @date:2022/5/5 22:52
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
class PublishHoleRepository(
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    companion object {
        private const val FOREST_SIZE = 40
    }

    @JvmField
    var pTypeForestList: MutableLiveData<ForestListsResponse?> = MutableLiveData()

    @JvmField
    var pJoinedForest: MutableLiveData<DetailTypeForestResponse> = MutableLiveData()

    @JvmField
    var pForestType: MutableLiveData<ForestTypeResponse> = MutableLiveData()

    @JvmField
    var pClickMsg: MutableLiveData<MsgResponse> = MutableLiveData()

    @JvmField
    var failed: MutableLiveData<String> = MutableLiveData()//放在总列表首位

    /**
     * 获取热门小树林
     */
    val hotForestForNetwork: Unit
        get() {
            NetworkApi.createService(PHRequestInterface::class.java, 2)
                .getHotForest(0, FOREST_SIZE).compose(NetworkApi.applySchedulers(object :
                    BaseObserver<DetailTypeForestResponse>() {
                    override fun onSuccess(requestedData: DetailTypeForestResponse) {
                        val forestListsResponse = pTypeForestList.value

                        //放在总列表首位
                        val detailTypeForestResponse = forestListsResponse!!.lists[0]
                        detailTypeForestResponse.forests = requestedData.forests
                        forestListsResponse.itemNumber = 0
                        pTypeForestList.postValue(forestListsResponse)
                    }

                    override fun onFailure(e: Throwable) {
                        failed.postValue((e as ResponseThrowable).message)
                    }
                }))
        }

    /**
     * 获取指定类型的小树林
     *
     * @param forest_type 小树林类型
     * @param location    在总列表中的位置
     */
    fun getTypeForestForNetwork(forest_type: String?, location: Int) {
        NetworkApi.createService(PHRequestInterface::class.java, 2)
            .getDetailTypeForest(forest_type, 0, FOREST_SIZE, false)
            .compose(NetworkApi.applySchedulers(object : BaseObserver<DetailTypeForestResponse>() {
                override fun onSuccess(list: DetailTypeForestResponse) {
                    // DetailTypeForestResponse detailTypeForestResponse=new DetailTypeForestResponse();
                    //detailTypeForestResponse.setList(list);

                    //放在列表指定位置
                    val forestListsResponse = pTypeForestList.value
                    val detailTypeForestResponse = forestListsResponse!!.lists[location]
                    detailTypeForestResponse.forests = list.forests
                    forestListsResponse.itemNumber = location
                    pTypeForestList.postValue(forestListsResponse)
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }

    /**
     * 获取加入的小树林
     */
    val joinedForestForNetwork: Unit
        get() {
            NetworkApi.createService(PHRequestInterface::class.java, 2)
                .joined(FOREST_SIZE, 0).compose(NetworkApi.applySchedulers(object :
                    BaseObserver<DetailTypeForestResponse>() {
                    override fun onSuccess(detailTypeForestResponse: DetailTypeForestResponse) {
                        pJoinedForest.postValue(detailTypeForestResponse)
                    }

                    override fun onFailure(e: Throwable) {
                        failed.postValue((e as ResponseThrowable).message)
                    }
                }))
        }//只有当数量大于等于0时才更新数据//加载完类型，为总列表初始化相同长度的数据

    /**
     * 获取小树林类型
     */
    val forestTypeForNetwork: Unit
        get() {
            NetworkApi.createService(PHRequestInterface::class.java, 2)
                .getType(0, FOREST_SIZE)
                .compose(NetworkApi.applySchedulers(object : BaseObserver<ForestTypeResponse>() {
                    override fun onSuccess(forestTypeResponse: ForestTypeResponse) {
                        //加载完类型，为总列表初始化相同长度的数据
                        if (pTypeForestList.value == null) {
                            val lists: MutableList<DetailTypeForestResponse> = ArrayList()
                            for (i in 0 until forestTypeResponse.types.size + 1) {
                                lists.add(DetailTypeForestResponse())
                            }
                            val forestListsResponse = ForestListsResponse()
                            forestListsResponse.itemNumber = -1 //只有当数量大于等于0时才更新数据
                            forestListsResponse.lists = lists
                            pTypeForestList.setValue(forestListsResponse)
                        }
                        pForestType.postValue(forestTypeResponse)
                    }

                    override fun onFailure(e: Throwable) {
                        failed.postValue((e as ResponseThrowable).message)
                    }
                }))
        }

    fun publishHole(content: String?, forest_name: Int) {
        NetworkApi.createService(PHRequestInterface::class.java, 2)
            .publishHole(
                Constant.BASE_URL + "holes?content=" + GetUrlUtil.getURLEncoderString(
                    content
                ) + "&forest_id=" + forest_name
            )
            .compose(NetworkApi.applySchedulers(object : BaseObserver<MsgResponse>() {
                override fun onSuccess(msg: MsgResponse) {
                    //返回结果
                    pClickMsg.postValue(msg)
                }

                override fun onFailure(e: Throwable) {
                    failed.postValue((e as ResponseThrowable).message)
                }
            }))
    }

    fun loadAllForests(): Flow<List<ForestBrief>> = flow {
        emit(
            hustHoleApiService.getAllForests(
                descend = true,
                limit = FOREST_SIZE,
                offset = 0,
            )
        )
    }.flowOn(dispatcher).catch { e ->
        e.printStackTrace()
    }

    fun loadJoinedForestsV2(): Flow<List<ForestBrief>> {
        return flow {
            emit(
                hustHoleApiService.getJoinedForests(
                    descend = true,
                    limit = FOREST_SIZE,
                    offset = 0,
                    timestamp = DateUtil.getDateTime()
                )
            )
        }.flowOn(dispatcher).catch { e ->
            e.printStackTrace()
        }
    }

}