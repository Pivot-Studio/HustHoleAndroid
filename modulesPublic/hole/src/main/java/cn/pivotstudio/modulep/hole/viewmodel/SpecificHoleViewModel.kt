package cn.pivotstudio.modulep.hole.viewmodel

import android.view.View
import cn.pivotstudio.modulep.hole.model.HoleResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.viewmodel.BaseViewModel
import cn.pivotstudio.modulep.hole.repository.HoleRepository
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulep.hole.model.MsgResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * @classname:HoleViewModel
 * @description:
 * @date:2022/5/8 15:57
 * @version:1.0
 * @author:
 */
class SpecificHoleViewModel(private var holeId: String) : BaseViewModel() {

    private var _hole = MutableStateFlow<HoleV2?>(null)
    val hole: StateFlow<HoleV2?> = _hole

    private var _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    val loadingState: StateFlow<ApiStatus> = _loadingState

    private var _sendingState = MutableSharedFlow<ApiResult>()
    val sendingState: SharedFlow<ApiResult> = _sendingState

    private var _replies = MutableStateFlow<List<ReplyWrapper>>(mutableListOf())
    val replies: StateFlow<List<ReplyWrapper>> = _replies

    private var _showingEmojiPad = MutableStateFlow(false)
    val showEmojiPad: StateFlow<Boolean> = _showingEmojiPad

    fun triggerEmojiPad() {
        viewModelScope.launch {
            _showingEmojiPad.emit(showEmojiPad.value.not())
        }
    }

    //网路数据
    var pInputText: MutableLiveData<ReplyResponse>
    var pClickMsg: MutableLiveData<MsgResponse>

    @JvmField
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>>

    //下面三项非网络请求所得，但是变化需要及时反馈在ui上
    var answered: ObservableField<ReplyResponse?>
    private var is_descend: ObservableField<Boolean?>
    var is_owner: ObservableField<Boolean?>
    var is_emoji: ObservableField<Boolean?>

    //进行数据请求的地方
    private val repository: HoleRepository = HoleRepository(holeId)
    fun usedEmojiList() = repository.usedEmojiForLocalDB

    @JvmName("getAnswered1")
    fun getAnswered(): ObservableField<ReplyResponse?> {
        if (answered.get() == null) {
            val base = ReplyResponse()
            base.alias = "洞主"
            base.is_mine = false
            base.reply_local_id = -1
            answered.set(base)
        }
        return answered
    }

    @JvmName("setAnswered1")
    fun setAnswered(answered: ObservableField<ReplyResponse?>) {
        this.answered = answered
    }

    fun getIs_owner(): ObservableField<Boolean?> {
        if (is_owner.get() == null) is_owner.set(false)
        return is_owner
    }

    fun setIs_owner(is_owner: ObservableField<Boolean?>) {
        this.is_owner = is_owner
    }

    fun getIs_descend(): ObservableField<Boolean?> {
        if (is_descend.get() == null) is_descend.set(false)
        return is_descend
    }

    fun setIs_descend(is_descend: ObservableField<Boolean?>) {
        this.is_descend = is_descend
    }

    fun getIs_emoji(): ObservableField<Boolean?> {
        if (is_emoji.get() == null) is_emoji.set(false)
        return is_emoji
    }

    fun setIs_emoji(is_emoji: ObservableField<Boolean?>) {
        this.is_emoji = is_emoji
    }

    fun inputText() = repository.getInputTextForLocalDB(holeId.toInt())

    fun saveInputText(text: String?) {
        val answered = getAnswered().get()
        repository.saveInputTextForLocalDB(
            holeId.toInt(),
            text,
            answered!!.alias,
            answered.is_mine,
            answered.reply_local_id
        )
    }

    fun updateInputText(text: String?) {
        val answered = getAnswered().get()
        repository.updateInputTextForLocalDB(
            holeId.toInt(),
            text,
            answered!!.alias,
            answered.is_mine,
            answered.reply_local_id
        )
    }

    fun deleteInputText() {
        repository.deleteInputTextForLocalDB()
    }

    fun sendAComment(content: String) {
        viewModelScope.launch {
            repository.sendAComment(content, null)
                .collectLatest {
                    _sendingState.emit(it)
                }
        }
    }

    fun loadMoreReplies() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadMoreReplies()
                .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                .onCompletion {
                    it?.let { _loadingState.emit(ApiStatus.ERROR) }
                }
                .catch { it.printStackTrace() }
                .collectLatest { holes ->
                    _replies.emit(_replies.value.toMutableList().apply {
                        addAll(holes)
                    })
                }
        }
    }


    fun loadHole() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.apply {
                loadHole().zip(loadReplies()) { hole, replies -> hole to replies }
                    .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                    .onCompletion { it?.let { _loadingState.emit(ApiStatus.ERROR) } }
                    .catch { it.printStackTrace() }
                    .collect {
                        _hole.emit(it.first)
                        _replies.emit(it.second)
                    }
            }
        }
    }

    fun delayLoadReplies() {
        viewModelScope.launch {
            delay(2000)
            loadHole()
        }
    }

    fun filterReplyOfHoleOwner() {
        viewModelScope.launch {
            _replies.emit(replies.value.filter {
                it.self.holeId == holeId
            })
        }
    }

    fun doneShowingEmojiPad() {
        _showingEmojiPad.value = false
    }

    /**
     * 涉及到网络请求相关的点击事件
     *
     * @param v        被点击的view
     * @param dataBean item的数据
     */
    fun itemClick(v: View, dataBean: HoleResponse) {
        val holeId = dataBean.hole_id
        when (v.id) {
            R.id.cl_hole_thumbup -> {
                val isThunbup = dataBean.is_thumbup
                val thumbupNum = dataBean.thumbup_num
                repository.thumbupForNetwork(holeId, thumbupNum, isThunbup, dataBean)
            }

            R.id.cl_hole_reply -> {
                val `as` = ReplyResponse()
                `as`.reply_local_id = -1
                `as`.is_mine = false
                `as`.alias = "洞主"
                answered.set(`as`)
            }

            R.id.cl_hole_follow -> {
                val isFollow = dataBean.is_follow
                val followNum = dataBean.follow_num
                repository.followForNetwork(holeId, followNum, isFollow, dataBean)
            }

            R.id.btn_hole_jumptodetailforest -> {
            }

            R.id.cl_hole_more_action -> {
                val isMine = dataBean.is_mine
                if (isMine) {
                    val dialog = DeleteDialog(v.context)
                    dialog.show()
                    dialog.setOptionsListener { v1: View? ->
                        repository.moreActionForNetwork(
                            holeId,
                            isMine,
                            -1,
                            "洞主"
                        )
                    }
                } else {
                    repository.moreActionForNetwork(holeId, isMine, -1, "洞主")
                }
                v.visibility = View.GONE
            }

            R.id.cl_hole_changesequence -> {
                val observableField = getIs_descend()
                observableField.set(!observableField.get()!!)
//                getListData(false)
            }

            R.id.tv_hole_content -> {
                val `as` = ReplyResponse()
                `as`.reply_local_id = -1
                `as`.is_mine = false
                `as`.alias = "洞主"
                answered.set(`as`)
            }
        }
    }

    /**
     * 构造函数
     */
    init {

        loadHole()

        pInputText = repository.pInputText
        pClickMsg = repository.pClickMsg
        pUsedEmojiList = repository.pUsedEmojiList
        failed = repository.failed
        answered = ObservableField()
        is_owner = ObservableField()
        is_descend = ObservableField()
        is_emoji = ObservableField()
    }
}