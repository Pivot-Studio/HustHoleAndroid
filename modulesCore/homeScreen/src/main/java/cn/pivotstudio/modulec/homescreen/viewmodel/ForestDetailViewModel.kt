package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailHolesLoadStatus
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForestDetailViewModel(
    val forestBrief: ForestBrief,
) : ViewModel() {

    val forestId: String = forestBrief.forestId
    val repository = ForestDetailRepository(forestId = forestId)

    private var _holesV2 = MutableStateFlow<List<HoleV2>>(emptyList())
    val holesV2: StateFlow<List<HoleV2>> = _holesV2

    private var _joined = MutableStateFlow(forestBrief.joined)
    val joined: StateFlow<Boolean> = _joined

    val state = repository.state
    val tip: MutableLiveData<String?> = repository.tip

    private var _loadLaterHoleId = ""

    init {
        loadHoles()
    }

    fun loadHoles() {
        viewModelScope.launch {
            repository.loadHolesByForestId(forestId)
                .collect { newItems ->
                    repository.state.value = ForestDetailHolesLoadStatus.DONE
                    _holesV2.emit(newItems)
                }
        }
    }

    fun loadHoleLater(holeId: String) {
        _loadLaterHoleId = holeId
    }

    fun loadMore() {
        viewModelScope.launch {
            repository.loadMoreHolesByForestId(forestId).collect { newItems ->
                repository.lastStartId += ForestDetailRepository.LIST_SIZE
                holesV2.value.toMutableList().apply { addAll(newItems) }.let {
                    _holesV2.emit(it)
                    repository.state.value = ForestDetailHolesLoadStatus.DONE
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

    fun deleteTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.deleteTheHole(hole).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        loadHoles()
                    }
                    is ApiResult.Error -> {
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun joinTheForest() {
        viewModelScope.launch {
            repository.joinTheForestV2()
                .collectLatest {
                    _joined.emit(it)
                }
        }
    }

    fun quitTheForest() {
        viewModelScope.launch {
            repository.quitTheForestV2()
                .collectLatest {
                    _joined.emit(it)
                }
        }
    }

    fun doneShowingTip() {
        tip.value = null
    }

    fun delayLoadHoles() {
        viewModelScope.launch {
            delay(2000)
            loadHoles()
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

}

class ForestDetailViewModelFactory(private val forestBrief: ForestBrief) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForestDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForestDetailViewModel(forestBrief) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}