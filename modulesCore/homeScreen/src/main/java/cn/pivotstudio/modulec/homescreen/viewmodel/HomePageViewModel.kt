package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.modulec.homescreen.repository.HomePageHoleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
    val holesV2: StateFlow<List<HoleV2>> = _holesV2

    private var _isLatestReply = MutableStateFlow(true)
    val isLatestReply: StateFlow<Boolean> = _isLatestReply

    private var _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    val loadingState: StateFlow<ApiStatus> = _loadingState

    private var _showingPlaceholder = MutableStateFlow<PlaceholderType?>(null)
    val showingPlaceholder = _showingPlaceholder.asStateFlow()

    private var _sortMode: String = NetworkConstant.SortMode.LATEST_REPLY

    private var _loadLaterHoleId = ""

    init {
        loadHolesV2()
    }

    fun loadHolesV2(sortMode: String = _sortMode) {
        isSearch = false
        when (sortMode) {
            NetworkConstant.SortMode.LATEST_REPLY -> _isLatestReply.value = true
            else -> _isLatestReply.value = false
        }

        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadHoles(sortMode)
                .onEach {
                    _loadingState.emit(ApiStatus.SUCCESSFUL)
                    _sortMode = sortMode
                }
                .catch { e ->
                    _loadingState.emit(ApiStatus.ERROR)
                    _showingPlaceholder.emit(PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                    e.printStackTrace()
                }
                .collectLatest {
                    it.forEach { hole ->
                        hole.isLatestReply = isLatestReply.value
                    }
                    _holesV2.emit(it)
                }
        }
    }

    fun loadMoreHoles(sortMode: String = _sortMode) {
        if (isSearch) {
            loadMoreSearchHoles()
            return
        }
        viewModelScope.launch {
            repository.loadMoreHoles(sortMode)
                .collectLatest {
                    _holesV2.emit(_holesV2.value.toMutableList().apply { addAll(it) })
                }
        }
    }

    fun searchHolesV2(queryKey: String) {
        queryKey.takeIf { it.isNotBlank() }?.let {
            viewModelScope.launch {
                _loadingState.emit(ApiStatus.LOADING)
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
        _loadLaterHoleId = holeId
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
                                tip.value = it.code.toString() + it.errorMessage
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
                                tip.value = it.code.toString() + it.errorMessage
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
                            tip.value = it.code.toString() + it.errorMessage
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
                        tip.value = it.code.toString() + it.errorMessage
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
            val holes = holesV2.value.toMutableList()
            val i = holes.indexOfFirst {
                it.holeId == _loadLaterHoleId
            }

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

    enum class PlaceholderType {
        PLACEHOLDER_NO_SEARCH_RESULT,
        PLACEHOLDER_NETWORK_ERROR
    }
}