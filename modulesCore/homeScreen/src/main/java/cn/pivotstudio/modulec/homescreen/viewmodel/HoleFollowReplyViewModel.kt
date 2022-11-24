package cn.pivotstudio.modulec.homescreen.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.repository.HoleFollowReplyRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 *@classname HoleFollowReplyViewModel
 * @description:
 * @date :2022/10/12 21:38
 * @version :1.0
 * @author
 */
class HoleFollowReplyViewModel : ViewModel() {
    val repository = HoleFollowReplyRepository()
    val tip: MutableLiveData<String?> = repository.tip

    private val _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    val loadingState: StateFlow<ApiStatus> = _loadingState
    private var _showingPlaceholder = MutableStateFlow<PlaceholderType?>(null)
    val showingPlaceholder: StateFlow<PlaceholderType?> = _showingPlaceholder

    private val _myHole = MutableStateFlow<List<HoleV2>>(mutableListOf())
    private val _myFollow = MutableStateFlow<List<HoleV2>>(mutableListOf())
    private val _myReply = MutableStateFlow<List<Reply>>(mutableListOf())

    val myHole: StateFlow<List<HoleV2>> = _myHole
    val myFollow: StateFlow<List<HoleV2>> = _myFollow
    val myReply: StateFlow<List<Reply>> = _myReply

    init {
        getMyHole()
        getMyFollow()
        getMyReply()
    }

    fun getMyHole() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            withTimeoutOrNull(MAX_REQUEST_TIME) {
                repository.getMyHole()
                    .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                    .catch { e ->
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        e.printStackTrace()
                    }
                    .collectLatest {
                        _myHole.emit(it)
                        if (it.isEmpty())
                            _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NO_CONTENT)
                    }
            }
            if (_loadingState.value == ApiStatus.LOADING) {
                _loadingState.emit(ApiStatus.ERROR)
                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
            }
        }
    }

    fun getMyFollow() {
        viewModelScope.launch {
            try{
                withTimeout(MAX_REQUEST_TIME) {
                    repository.getMyFollow().collect {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                _myFollow.emit(it.data as List<HoleV2>)
                                if (_myFollow.value.isEmpty())
                                    _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NO_CONTENT)
                            }
                            is ApiResult.Error -> {
                                tip.value = it.code.toString() + it.errorMessage
                                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                            }
                            else -> {}
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                if (_loadingState.value == ApiStatus.LOADING) {
                    _loadingState.emit(ApiStatus.ERROR)
                    _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                }
            }

        }
    }

    fun getMyReply() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            withTimeoutOrNull(MAX_REQUEST_TIME) {
                repository.getMyReply()
                    .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                    .catch { e ->
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        e.printStackTrace()
                    }
                    .collectLatest {
                        _myReply.emit(it)
                        if (it.isEmpty())
                            _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NO_CONTENT)
                    }
            }
            if (_loadingState.value == ApiStatus.LOADING) {
                _loadingState.emit(ApiStatus.ERROR)
                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
            }
        }
    }

    fun loadMoreHole() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            withTimeoutOrNull(MAX_REQUEST_TIME) {
                repository.loadMoreHole()
                    .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                    .catch { e ->
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        e.printStackTrace()
                    }
                    .collectLatest {
                        _myHole.emit(_myHole.value.toMutableList().apply { addAll(it) })
                    }
            }
            if (_loadingState.value == ApiStatus.LOADING) {
                _loadingState.emit(ApiStatus.ERROR)
                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
            }
        }
    }

    fun loadMoreFollow() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            withTimeoutOrNull(MAX_REQUEST_TIME) {
                repository.loadMoreFollow().collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            _myFollow.emit(_myFollow.value.toMutableList().apply { addAll(it.data as List<HoleV2>) })
                        }
                        is ApiResult.Error -> {
                            _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                            tip.value = it.code.toString() + it.errorMessage
                        }
                        else -> {}
                    }
                }
            }
            if (_loadingState.value == ApiStatus.LOADING) {
                _loadingState.emit(ApiStatus.ERROR)
                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
            }
        }
    }

    fun loadMoreReply() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            withTimeoutOrNull(MAX_REQUEST_TIME) {
                repository.loadMoreReply()
                    .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                    .catch { e ->
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        e.printStackTrace()
                    }
                    .collectLatest {
                        _myReply.emit(_myReply.value.toMutableList().apply { addAll(it) })
                    }
            }
            if (_loadingState.value == ApiStatus.LOADING) {
                _loadingState.emit(ApiStatus.ERROR)
                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
            }
        }
    }

    fun deleteTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.deleteTheHole(hole).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        tip.value = "删除成功！"
                        getMyHole()
                    }
                    is ApiResult.Error -> {
                        tip.value = it.code.toString() + it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteReply(replyId: String) {
        viewModelScope.launch {
            repository.deleteReply(replyId)
                .collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            tip.value = "删除成功！"
                            getMyReply()
                        }
                        is ApiResult.Error -> {
                            tip.value = it.code.toString() + it.errorMessage
                        }
                        else -> {}
                    }
                }
        }
    }

    fun doneShowingTip() {
        repository.tip.value = null
    }

    enum class PlaceholderType {
        PLACEHOLDER_NO_CONTENT,
        PLACEHOLDER_NETWORK_ERROR
    }

    companion object {
        const val MAX_REQUEST_TIME: Long = 5000
    }
}