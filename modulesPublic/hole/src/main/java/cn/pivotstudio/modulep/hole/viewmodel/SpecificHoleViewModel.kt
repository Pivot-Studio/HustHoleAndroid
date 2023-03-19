package cn.pivotstudio.modulep.hole.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import cn.pivotstudio.modulep.hole.repository.HoleRepository
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

/**
 * @classname:HoleViewModel
 * @description:
 * @date:2022/5/8 15:57
 * @version:1.0
 * @author:
 */
class SpecificHoleViewModel(
    private var holeId: String,
    private val finish: () -> Unit
) : ViewModel() {

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

    private var _showingPlaceholder = MutableStateFlow(false)
    val showingPlaceholder = _showingPlaceholder.asStateFlow()

    private var _filteringOwner = MutableStateFlow(false)
    val filteringOwner = _filteringOwner.asStateFlow()

    private var _descend = MutableStateFlow(true)
    val descend = _descend.asStateFlow()

    fun reportTheHole() {
        ARouter.getInstance().build("/report/ReportActivity")
            .withString(Constant.HOLE_ID, _hole.value?.holeId)
            .withString(Constant.ALIAS, "洞主")
            .navigation()
    }

    fun triggerEmojiPad() {
        viewModelScope.launch {
            _showingEmojiPad.emit(showEmojiPad.value.not())
        }
    }

    fun triggerSort() {
        viewModelScope.launch {
            _descend.emit(descend.value.not())
        }

        viewModelScope.launch {
            descend.collectLatest {
                loadHole()
            }
        }
    }

    fun filterHoleOfOwner() {
        viewModelScope.launch {
            _filteringOwner.emit(filteringOwner.value.not())
        }

        viewModelScope.launch {
            filteringOwner.collectLatest {
                when (it) {
                    true -> {
                        _replies.emit(replies.value.filter { reply ->
                            reply.self.nickname == "洞主"
                        })
                    }
                    false -> {
                        loadHole()
                    }
                }
            }
        }

    }


    //网路数据
    var pInputText: MutableLiveData<ReplyResponse>

    @JvmField
    var pUsedEmojiList: MutableLiveData<LinkedList<Int>>

    private var _commentToReply = MutableStateFlow<Reply?>(null)
    val commentToReply = _commentToReply.asStateFlow()

    //进行数据请求的地方
    private val repository = HoleRepository(holeId)

    fun usedEmojiList() = repository.usedEmojiForLocalDB

    fun inputText() = repository.getInputTextForLocalDB(holeId.toInt())

    fun saveInputText(text: String?) {
        val answered = commentToReply.value
        repository.saveInputTextForLocalDB(
            holeId.toInt(),
            text,
            answered!!.nickname,
            answered.mine,
            answered.replyId.toInt()
        )
    }

    fun updateInputText(text: String?) {
        val answered = commentToReply.value
        repository.updateInputTextForLocalDB(
            holeId.toInt(),
            text,
            answered!!.nickname,
            answered.mine,
            answered.replyId.toInt()
        )
    }

    fun deleteInputText() {
        repository.deleteInputTextForLocalDB()
    }

    fun sendAComment(content: String) {
        viewModelScope.launch {
            repository.sendAComment(content, _commentToReply.value?.replyId)
                .collectLatest {
                    _sendingState.emit(it)
                }
        }
    }

    fun loadMoreReplies() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadMoreReplies(descend.value).collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _filteringOwner.emit(false)
                        _replies.emit(_replies.value.toMutableList().apply {
                            addAll(it.data as Collection<ReplyWrapper>)
                        })
                    }
                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadHole() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.apply {
                loadHole().collect {loadHoleResult ->
                    when(loadHoleResult) {
                        is ApiResult.Success<*> -> {
                            _hole.emit(loadHoleResult.data as HoleV2)
                            loadReplies(descend.value).collect {loadRepliesResult ->
                                when(loadRepliesResult) {
                                    is ApiResult.Success<*> -> {
                                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                                        _replies.emit(loadRepliesResult.data as List<ReplyWrapper>)
                                        if(_replies.value.isEmpty()) {
                                            _showingPlaceholder.emit(true)
                                        }
                                    }
                                    is ApiResult.Error -> {
                                        _loadingState.emit(ApiStatus.ERROR)
                                        _sendingState.emit(loadRepliesResult)
                                    }
                                    else -> {}
                                }
                            }
                        }
                        is ApiResult.Error -> {
                            if(loadHoleResult.code == 1006)
                                finish()
                            _sendingState.emit(loadHoleResult)
                            _loadingState.emit(ApiStatus.ERROR)
                        }
                        else -> {}
                    }
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
        viewModelScope.launch {
            _showingEmojiPad.emit(false)
        }
    }

    fun replyTo(reply: Reply) {
        viewModelScope.launch {
            _commentToReply.emit(reply)
        }
    }

    fun replyToOwner() {
        viewModelScope.launch {
            _commentToReply.emit(null)
        }
    }

    fun followTheHole(hole: HoleV2 = _hole.value!!) {
        viewModelScope.launch {
            if (hole.isFollow) {
                repository.unFollowTheHole(hole)
                    .collect {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                _hole.emit(
                                    hole.copy(
                                        isFollow = hole.isFollow.not(),
                                        followCount = hole.followCount - 1
                                    )
                                )
                            }
                            is ApiResult.Error -> {
                                _sendingState.emit(it)
                            }
                            else -> {}
                        }
                    }
            } else {
                repository.followTheHole(hole)
                    .collect {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                _hole.emit(
                                    hole.copy(
                                        isFollow = hole.isFollow.not(),
                                        followCount = hole.followCount + 1
                                    )
                                )
                            }
                            is ApiResult.Error -> {
                                _sendingState.emit(it)
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    fun giveALikeToTheReply(reply: Reply) {
        viewModelScope.launch {
            repository.giveALikeToTheReply(reply)
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            likeTheReply(reply)
                        }
                        is ApiResult.Error -> {
                            _sendingState.emit(it)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun giveALikeToTheHole(hole: HoleV2 = _hole.value!!) {
        viewModelScope.launch {
            repository.giveALikeToTheHole(hole)
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            _hole.emit(
                                hole.copy(
                                    liked = hole.liked.not(),
                                    likeCount = hole.likeCount.plus(
                                        if (hole.liked) -1 else 1
                                    )
                                )
                            )
                        }
                        is ApiResult.Error -> {
                            _sendingState.emit(it)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun deleteTheHole(hole: HoleV2 = _hole.value!!) {
        viewModelScope.launch {
            repository.deleteTheHole(hole).collect {
                when (it) {
                    is ApiResult.Success<*> -> {

                    }
                    is ApiResult.Error -> {
                        _sendingState.emit(it)
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteTheReply(reply: Reply) {
        viewModelScope.launch {
            repository.deleteTheReply(reply).collect {
                when (it) {
                    is ApiResult.Success<*> -> {

                    }
                    is ApiResult.Error -> {
                        _sendingState.emit(it)
                    }
                    else -> {}
                }
            }
        }
    }

    fun refreshTheReply(reply: Reply) {
        viewModelScope.launch {
            val newItems = replies.value.toMutableList()
            val i = newItems.indexOfFirst { newReply ->
                reply.replyId == newReply.self.replyId
            }

            newItems[i] = newItems[i].copy(
                self = reply
            )
            _replies.emit(newItems)
        }
    }

    /**
     * 点赞请求成功后端上直接更改显示信息, 避免再次请求后端的耗时和不稳定
     */
    suspend fun likeTheReply(reply: Reply) {
        val newItems = replies.value.toMutableList()
        val i = newItems.indexOfFirst { newReply ->
            reply.replyId == newReply.self.replyId
        }

        val newSelfReply = newItems[i].self.copy(
            thumb = reply.thumb.not(),
            likeCount = reply.likeCount + if (reply.thumb) -1 else 1
        )

        newItems[i] = newItems[i].copy(
            self = newSelfReply
        )
        _replies.emit(newItems)
    }

    /**
     * 构造函数
     */
    init {

        loadHole()

        pInputText = repository.pInputText
        pUsedEmojiList = repository.pUsedEmojiList
    }
}