package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.modulec.homescreen.repository.HomePageHoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeHoleViewModel : ViewModel() {
    private val repository = HomePageHoleRepository()
    private var _holesV2 = MutableStateFlow<List<HoleV2>>(mutableListOf())
    val holesV2 = _holesV2.asStateFlow()

    private var _isLatestReply = MutableStateFlow(true)
    val isLatestReply: StateFlow<Boolean> = _isLatestReply.asStateFlow()

    private var _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    val loadingState: StateFlow<ApiStatus> = _loadingState

    private var _showingPlaceholder = MutableStateFlow<HomePageViewModel.PlaceholderType?>(null)
    val showingPlaceholder = _showingPlaceholder.asStateFlow()

    private var _sortMode: String = NetworkConstant.SortMode.REC

    private var _loadLaterHoleId = ""
    val tip: MutableLiveData<String?> = repository.tip
    var type = -1

    fun loadRecHoles(sortMode: String = _sortMode) {
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
                        _sortMode = sortMode
                        (it.data as List<HoleV2>).forEach { hole ->
                            hole.isLatestReply = isLatestReply.value
                        }
                        _holesV2.emit(it.data as List<HoleV2>)
                    }
                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        _showingPlaceholder.emit(HomePageViewModel.PlaceholderType.PLACEHOLDER_NETWORK_ERROR)
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadMoreRecHoles(sortMode: String = _sortMode) {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadMoreRecHoles(sortMode).collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _holesV2.emit(_holesV2.value.toMutableList().apply { addAll(it.data as List<HoleV2>) })
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

    fun refreshLoadLaterHole(
        isThumb: Boolean,
        replied: Boolean,
        followed: Boolean,
        thumbNum: Long,
        replyNum: Long,
        followNum: Long
    ) {
        viewModelScope.launch {
            var i = _holesV2.value.indexOfFirst {
                it.holeId == _loadLaterHoleId
            }
            if(i == -1) {
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

    fun deleteTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.deleteTheHole(hole).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        loadRecHoles()
                    }
                    is ApiResult.Error -> {
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadHoleLater(holeId: String) {
        viewModelScope.launch {
            _loadLaterHoleId = holeId
        }
    }

    fun doneShowingTip() {
        tip.value = null
    }
}