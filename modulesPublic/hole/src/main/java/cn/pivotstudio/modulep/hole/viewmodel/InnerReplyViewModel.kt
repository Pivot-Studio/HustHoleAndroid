package cn.pivotstudio.modulep.hole.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.modulep.hole.repository.InnerReplyRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InnerReplyViewModel(private val baseReply: Reply) : ViewModel() {

    private val repo = InnerReplyRepo(baseReply.holeId)

    private var _reply = MutableStateFlow(baseReply)
    val reply: StateFlow<Reply> = _reply

    private var _commentToReply = MutableStateFlow(baseReply)
    val commentToReply = _commentToReply.asStateFlow()

    private var _innerReplies = MutableStateFlow(listOf<Reply>())
    val innerReplies: StateFlow<List<Reply>> = _innerReplies

    private var _showingEmojiPad = MutableStateFlow(false)
    val showEmojiPad: StateFlow<Boolean> = _showingEmojiPad

    private var _sendingState = MutableSharedFlow<ApiResult>()
    val sendingState: SharedFlow<ApiResult> = _sendingState

    private var _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    val loadingState: StateFlow<ApiStatus> = _loadingState


    fun triggerEmojiPad() {
        viewModelScope.launch {
            _showingEmojiPad.emit(showEmojiPad.value.not())
        }
    }

    fun loadSecondLvReplies() {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingState.emit(ApiStatus.LOADING)
            repo.loadInnerReplies(reply.value.replyId)
                .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                .onCompletion { it?.let { _loadingState.emit(ApiStatus.ERROR) } }
                .catch { it.printStackTrace() }
                .collectLatest {
                    _innerReplies.emit(it)
                }
        }
    }

    fun loadMoreReplies() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repo.loadMoreReplies(reply.value.replyId)
                .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                .onCompletion {
                    it?.let { _loadingState.emit(ApiStatus.ERROR) }
                }
                .catch { it.printStackTrace() }
                .collectLatest { newInnerReplies ->
                    _innerReplies.emit(innerReplies.value.toMutableList() + newInnerReplies)
                }
        }
    }

    fun sendAInnerComment(content: String) {
        viewModelScope.launch {
            repo.sendAComment(content, _commentToReply.value.replyId)
                .collectLatest {
                    _sendingState.emit(it)
                }
        }
    }

    fun deleteTheReply(reply: Reply) {
        viewModelScope.launch {
            repo.deleteTheReply(reply).collect {
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

    /** 改变回复对象 */
    fun replyTo(reply: Reply) {
        viewModelScope.launch {
            _commentToReply.emit(reply)
        }
    }

    /** 回复楼主，默认值就是楼主 */
    fun replyToOwner() {
        viewModelScope.launch {
            _commentToReply.emit(baseReply)
        }
    }

    fun delayLoadReplies() {
        viewModelScope.launch {
            delay(2000)
            loadSecondLvReplies()
        }
    }

    fun doneShowingEmojiPad() {
        viewModelScope.launch {
            _showingEmojiPad.emit(false)
        }
    }

    init {
        loadSecondLvReplies()
    }

}