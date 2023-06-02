package cn.pivotstudio.modulep.hole.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.HustHoleApi
import cn.pivotstudio.husthole.moduleb.network.HustHoleApiService
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.husthole.moduleb.network.model.RequestBody
import cn.pivotstudio.husthole.moduleb.network.util.DateUtil
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.database.bean.Hole
import cn.pivotstudio.moduleb.database.repository.CustomDisposable
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.DB
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import retrofit2.Response
import java.util.*

/**
 * @classname:HoleRepository
 * @description:
 * @date:2022/5/8 13:32
 * @version:1.0
 * @author:
 */
class HoleRepository(
    private val holeId: String,
    private val hustHoleApiService: HustHoleApiService = HustHoleApi.retrofitService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    var lastOffset: Int = 0,
    private var lastTimeStamp: String = DateUtil.getDateTime()
) {

    companion object {
        const val LIST_SIZE = 20
    }

    var pInputText: MutableLiveData<ReplyResponse> = MutableLiveData()
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>> = MutableLiveData()
    private var hole: Hole? = null
    val tip = MutableLiveData<String?>()
    fun getInputTextForLocalDB(hole_id: Int?) {
        val flowable = DB!!.holeDao().findById(
            hole_id!!
        )
        CustomDisposable.addDisposable(flowable) { holeEt: Hole? ->
            if (holeEt != null) {
                hole = holeEt
                val requestedData = ReplyResponse()
                requestedData.content = holeEt.content
                requestedData.alias = holeEt.alias
                requestedData.is_mine = holeEt.is_mine
                requestedData.reply_local_id = holeEt.reply_local_id
                pInputText.postValue(requestedData)
            }
        }
    }

    val usedEmojiForLocalDB: Unit
        get() {
            val mmkvUtil = MMKVUtil.getMMKV(BaseApplication.context)
            val list = mmkvUtil.getArray(Constant.UsedEmoji, 0) as LinkedList<Int>
            pUsedEmojiList.postValue(list)
        }

    fun saveInputTextForLocalDB(
        hole_id: Int?,
        text: String?,
        alias: String?,
        is_mine: Boolean?,
        reply_local_id: Int?
    ) {
        val hole = Hole(
            hole_id!!, text, alias, is_mine, reply_local_id
        )
        val insert = DB!!.holeDao().insert(hole)
        CustomDisposable.addDisposable<Any>(insert) {
            Log.d(
                "数据库",
                "saveInputTextForLocalDB:数据保存成功"
            )
        }
    }

    fun updateInputTextForLocalDB(
        hole_id: Int?,
        text: String?,
        alias: String?,
        is_mine: Boolean?,
        reply_local_id: Int?
    ) {
        val hole = Hole(
            hole_id!!, text, alias, is_mine, reply_local_id
        )
        val insert = DB!!.holeDao().update(hole)
        CustomDisposable.addDisposable<Any>(insert) {
            Log.d(
                "数据库",
                "saveInputTextForLocalDB:数据更新成功"
            )
        }
    }

    fun deleteInputTextForLocalDB() {
        val insert = DB!!.holeDao().delete(hole)
        CustomDisposable.addDisposable<Any>(insert) {
            Log.d(
                "数据库",
                "saveInputTextForLocalDB:数据删除成功"
            )
        }
    }

    fun sendAComment(content: String, repliedId: String?): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response =
            hustHoleApiService.sendAComment(
                comment = RequestBody.Comment(
                    holeId = holeId,
                    repliedId = repliedId,
                    content = content
                )
            )
        checkResponse(response, this)
    }.flowOn(dispatcher)


    fun loadHole(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.loadTheHole(holeId)
        checkResponse(response, this)
    }.flowOn(dispatcher).onEach {
        lastTimeStamp = DateUtil.getDateTime()
    }.catch { e ->
        e.printStackTrace()
    }

    fun loadReplies(descend: Boolean = true): Flow<ApiResult> = flow {
        lastTimeStamp = DateUtil.getDateTime()
        emit(ApiResult.Loading())
        val response = hustHoleApiService.getHoleReplies(
            holeId = holeId,
            timestamp = lastTimeStamp,
            limit = LIST_SIZE,
            descend = descend
        )
        checkResponse(response, this)
    }.flowOn(dispatcher).catch {
        it.printStackTrace()
    }.onEach {
        lastOffset = 0
    }

    fun loadMoreReplies(descend: Boolean = true): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.getHoleReplies(
            holeId = holeId,
            timestamp = lastTimeStamp,
            offset = lastOffset + LIST_SIZE,
            limit = LIST_SIZE,
            descend = descend
        )
        checkResponse(response, this)
    }.flowOn(dispatcher)

    fun giveALikeToTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = if (hole.liked) {
                hustHoleApiService.unLike(
                    like = RequestBody.LikeRequest(holeId = hole.holeId)
                )
            } else {
                hustHoleApiService.giveALikeTo(
                    like = RequestBody.LikeRequest(holeId = hole.holeId)
                )
            }

            checkResponse(response, this)
        }.flowOn(dispatcher).catch { it.printStackTrace() }
    }

    fun giveALikeToTheReply(reply: Reply): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val likeRequest = RequestBody.LikeRequest(
                holeId = reply.holeId,
                replyId = reply.replyId
            )

            val response = if (reply.thumb) {
                hustHoleApiService.unLike(like = likeRequest)
            } else {
                hustHoleApiService.giveALikeTo(like = likeRequest)
            }
            checkResponse(response, this)
        }.flowOn(dispatcher).catch { it.printStackTrace() }
    }

    fun followTheHole(hole: HoleV2): Flow<ApiResult> {
        return flow {
            emit(ApiResult.Loading())
            val response = hustHoleApiService
                .followTheHole(RequestBody.HoleId(hole.holeId))
            checkResponse(response, this)
        }.catch { it.printStackTrace() }.flowOn(dispatcher)
    }

    fun unFollowTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .unFollowTheHole(RequestBody.HoleId(hole.holeId))
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }


    fun deleteTheHole(hole: HoleV2): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService
            .deleteTheHole(hole.holeId)
        checkResponse(response, this)
    }


    fun deleteTheReply(reply: Reply): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val response = hustHoleApiService.deleteTheReply(reply.replyId)
        checkResponse(response, this)
    }.flowOn(dispatcher).catch { it.printStackTrace() }


    private suspend fun <T> checkResponse(
        response: Response<T>?,
        flow: FlowCollector<ApiResult>
    ) {
        if (response?.isSuccessful == true) {
            flow.emit(ApiResult.Success(data = response.body()))
        } else {
            val json = response?.errorBody()?.string()
            val jsonObject = json?.let { JSONObject(it) }
            val returnCondition = jsonObject?.getString("errorMsg")
            val errorCode = jsonObject?.getString("errorCode")
            flow.emit(
                ApiResult.Error(
                    code = errorCode?.toInt() ?: response?.code() ?: 0,
                    errorMessage = returnCondition
                )
            )
            response?.errorBody()?.close()
        }
    }
}