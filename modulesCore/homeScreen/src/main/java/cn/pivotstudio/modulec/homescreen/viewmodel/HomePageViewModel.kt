package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.modulec.homescreen.repository.HomePageHoleRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @classname:HomePageViewModel
 * @description:
 * @date:2022/5/2 23:09
 * @version:1.0
 * @author:
 */
class HomePageViewModel : ViewModel() {
    private val repository = HomePageHoleRepository()

    private var _holesV2 = MutableStateFlow<List<HoleV2>>(mutableListOf())
    val holesV2 = _holesV2.asStateFlow()

    private var _isLatestReply = MutableStateFlow(true)
    val isLatestReply: StateFlow<Boolean> = _isLatestReply.asStateFlow()

    private var _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    val loadingState: StateFlow<ApiStatus> = _loadingState

    private var _showingPlaceholder = MutableStateFlow<PlaceholderType?>(null)
    val showingPlaceholder = _showingPlaceholder.asStateFlow()

    private val _sortMode = MutableLiveData(NetworkConstant.SortMode.LATEST_REPLY)
    val sortMode: LiveData<String> = _sortMode

    private var _loadLaterHoleId = ""

    fun getMyFollow() {
        viewModelScope.launch {
            try {
                withTimeout(HoleFollowReplyViewModel.MAX_REQUEST_TIME) {
                    _loadingState.emit(ApiStatus.LOADING)
                    repository.getMyFollow().collect {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                _loadingState.emit(ApiStatus.SUCCESSFUL)
                                _holesV2.emit(it.data as List<HoleV2>)
                                if (_holesV2.value.isEmpty())
                                    _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NO_CONTENT)
                            }
                            is ApiResult.Error -> {
                                _loadingState.emit(ApiStatus.ERROR)
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

    fun loadMoreFollow() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            withTimeoutOrNull(HoleFollowReplyViewModel.MAX_REQUEST_TIME) {
                repository.loadMoreFollow().collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            _loadingState.emit(ApiStatus.SUCCESSFUL)
                            if((it.data as List<*>).isEmpty()) {
                                tip.value = "到底了哟~"
                            }else {
                                _holesV2.emit(
                                    _holesV2.value.toMutableList()
                                        .apply { addAll(it.data as List<HoleV2>) })
                            }
                        }
                        is ApiResult.Error -> {
                            _loadingState.emit(ApiStatus.ERROR)
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

    fun loadRecHoles(sortMode: String = _sortMode.value!!) {
        when (sortMode) {
            NetworkConstant.SortMode.LATEST_REPLY -> _isLatestReply.value = true
            else -> _isLatestReply.value = false
        }
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadRecHoles(sortMode).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _sortMode.value = sortMode
                        (it.data as List<HoleV2>).forEach { hole ->
                            hole.isLatestReply = isLatestReply.value
                        }
                        _holesV2.emit(it.data as List<HoleV2>)
                    }
                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadMoreRecHoles(sortMode: String = _sortMode.value!!) {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadMoreRecHoles(sortMode).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        if ((it.data as List<*>).isEmpty()) {
                            tip.value = "到底了哟~"
                        } else {
                            _holesV2.emit(
                                _holesV2.value.toMutableList()
                                    .apply { addAll(it.data as List<HoleV2>) })
                        }
                    }
                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadHolesV2(sortMode: String = _sortMode.value!!) {
        isSearch = false
        when (sortMode) {
            NetworkConstant.SortMode.LATEST_REPLY -> _isLatestReply.value = true
            else -> _isLatestReply.value = false
        }
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadHoles(sortMode).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _sortMode.value = sortMode
                        (it.data as List<HoleV2>).forEach { hole ->
                            hole.isLatestReply = isLatestReply.value
                        }
                        _holesV2.emit(it.data as List<HoleV2>)
                    }
                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadMoreHoles(sortMode: String = _sortMode.value!!) {
        if (isSearch) {
            loadMoreSearchHoles()
            return
        }
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadMoreHoles(sortMode).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _holesV2.emit(
                            _holesV2.value.toMutableList()
                                .apply { addAll(it.data as List<HoleV2>) })
                    }
                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun searchHolesV2(queryKey: String) {
        queryKey.takeIf { it.isNotBlank() }?.let {
            viewModelScope.launch {
                _loadingState.emit(ApiStatus.LOADING)
                if (queryKey.isDigitsOnly()) {
                    repository.loadTheHole(queryKey).collectLatest {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                _loadingState.emit(ApiStatus.SUCCESSFUL)
                                _holesV2.emit(listOf(it.data as HoleV2))
                            }
                            is ApiResult.Error -> {
                                tip.value = it.errorMessage
                                _loadingState.emit(ApiStatus.ERROR)
                                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                            }
                            else -> {}
                        }
                    }
                } else {
                    repository.searchHolesBy(it)
                        .onEach { _loadingState.emit(ApiStatus.SUCCESSFUL) }
                        .catch {
                            tip.value = it.message
                            _loadingState.emit(ApiStatus.ERROR)
                            _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        }
                        .collectLatest { holes ->
                            if (holes.isEmpty()) {
                                _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NO_SEARCH_RESULT)
                            }
                            _holesV2.emit(holes)
                        }
                }
            }
        }
    }

    private fun loadMoreSearchHoles() {
        viewModelScope.launch {
            repository.loadMoreSearchHoles(searchKeyword)
                .catch { e ->
                    _loadingState.emit(ApiStatus.ERROR)
                    _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                    tip.value = e.message
                    e.printStackTrace()
                }
                .collectLatest {
                    _holesV2.emit(_holesV2.value.toMutableList().apply { addAll(it) })
                }
        }
    }

    fun loadHoleLater(holeId: String) {
        viewModelScope.launch {
            _loadLaterHoleId = holeId
        }
    }

    fun followTheHole(hole: HoleV2) {
        viewModelScope.launch {
            if (hole.isFollow) {
                repository.unFollowTheHole(hole)
                    .collect {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                val newItems = _holesV2.value.toMutableList()
                                val i = newItems.indexOfFirst { newHole ->
                                    hole.holeId == newHole.holeId
                                }

                                newItems[i] = newItems[i].copy(
                                    isFollow = hole.isFollow.not(),
                                    followCount = hole.followCount - 1
                                )

                                _holesV2.emit(newItems)
                            }
                            is ApiResult.Error -> {
                                tip.value = it.errorMessage
                            }
                            else -> {}
                        }
                    }
            } else {
                repository.followTheHole(hole)
                    .collect {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                val newItems = _holesV2.value.toMutableList()
                                val i = newItems.indexOfFirst { newHole ->
                                    hole.holeId == newHole.holeId
                                }

                                newItems[i] = newItems[i].copy(
                                    isFollow = hole.isFollow.not(),
                                    followCount = hole.followCount + 1
                                )

                                _holesV2.emit(newItems)
                            }
                            is ApiResult.Error -> {
                                tip.value = it.errorMessage
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    fun giveALikeToTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.giveALikeToTheHole(hole)
                .collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            likeTheHole(hole)
                        }
                        is ApiResult.Error -> {
                            tip.value = it.errorMessage
                        }
                        else -> {}
                    }
                }
        }
    }

    fun deleteTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.deleteTheHole(hole).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        loadHolesV2()
                    }
                    is ApiResult.Error -> {
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun likeTheHole(hole: HoleV2) {
        val newItems = _holesV2.value.toMutableList()
        val i = newItems.indexOfFirst { newHole ->
            hole.holeId == newHole.holeId
        }

        newItems[i] = newItems[i].copy(
            liked = hole.liked.not(),
            likeCount = hole.likeCount.plus(
                if (hole.liked) -1 else 1
            )
        )
        _holesV2.emit(newItems)
    }


    val tip: MutableLiveData<String?> = repository.tip
    private var mIsSearch: Boolean = false //是否是搜索状态
    private var mSearchKeyword: String = "" //搜索关键词
    var isSearch: Boolean
        get() {
            return mIsSearch
        }
        set(pIsSearch) {
            mIsSearch = pIsSearch
        }


    var searchKeyword: String
        get() {
            return mSearchKeyword
        }
        set(pSearchKeyword) {
            mSearchKeyword = pSearchKeyword
        }

    fun doneShowingTip() {
        tip.value = null
    }

    fun refreshLoadLaterHole(
        isThumb: Boolean,
        replied: Boolean,
        followed: Boolean,
        thumbNum: Long,
        replyNum: Long,
        followNum: Long
    ) {
        viewModelScope.launch {
            if (_holesV2.value.isNotEmpty()) {
                var i = _holesV2.value.indexOfFirst {
                    it.holeId == _loadLaterHoleId
                }
                if (i == -1) {
                    i = 0
                    tip.value = _holesV2.value[0].holeId
                }
                val holes = _holesV2.value.toMutableList()
                holes[i] = holes[i].copy(
                    liked = isThumb,
                    isReply = replied,
                    isFollow = followed,
                    likeCount = thumbNum,
                    replyCount = replyNum,
                    followCount = followNum
                )
                _holesV2.emit(holes)
            }
        }
    }

    enum class PlaceholderType {
        PLACEHOLDER_NO_CONTENT,
        PLACEHOLDER_NO_SEARCH_RESULT,
        PLACEHOLDER_NETWORK_ERROR
    }
}