package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.modulec.homescreen.repository.ForestRepository
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
class ForestViewModel : ViewModel() {
    private val repository = ForestRepository()

    val holesLoadState: MutableLiveData<LoadStatus?> = repository.holeState
    val headerLoadState: MutableLiveData<LoadStatus> = repository.headerLoadState

    private var _holesV2 = MutableStateFlow<List<HoleV2>>(mutableListOf())
    val holesV2: StateFlow<List<HoleV2>> = _holesV2

    private var _forestsV2 = MutableStateFlow<List<ForestBrief>>(mutableListOf())
    val forestsV2: StateFlow<List<ForestBrief>> = _forestsV2

    private var _shouldRefreshHeader = false

    private var _loadLaterHoleId = ""

    val tip: MutableLiveData<String?> = repository.tip

    init {
        loadHolesAndHeads()
    }

    fun loadHeaderLater() {
        _shouldRefreshHeader = true
    }

    fun tryLoadNewHeader() {
        if (_shouldRefreshHeader) {
            loadJoinedForestsV2()
            _shouldRefreshHeader = false
        }
    }

    fun loadHolesAndHeads() {
        viewModelScope.launch {
            repository.apply {
                loadJoinedForestsV2().zip(loadForestHolesV2()) { forests, holes ->
                    packForestAvatarUrlToHoles(forests, holes)
                    forests to holes
                }.collect {
                    holesLoadState.value = LoadStatus.DONE
                    _forestsV2.emit(it.first)
                    _holesV2.emit(it.second)
                }
            }
        }
    }

    fun loadMoreForestHoles() {
        holesLoadState.value = LoadStatus.LOADING
        viewModelScope.launch {
            repository.loadMoreForestHolesV2()
                .collect {
                    val newHoles = _holesV2.value.toMutableList() + it
                    packForestAvatarUrlToHoles(
                        holes = newHoles
                    )
                    _holesV2.emit(newHoles)
                    holesLoadState.value = LoadStatus.DONE
                }
        }
    }

    fun getForestById(forestId: String): ForestBrief = _forestsV2.value.first {
        it.forestId == forestId
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

    fun doneShowingTip() {
        tip.value = null
    }

    fun showPlaceHolder() {
        repository.holeState.value = null
    }

    fun loadHoleLater(holeId: String) {
        _loadLaterHoleId = holeId
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

    fun loadHolesV2() {
        viewModelScope.launch {
            repository.loadForestHolesV2()
                .map { holes ->
                    holes.forEach { hole ->
                        hole.forestAvatarUrl = _forestsV2.value.find {
                            it.forestId == hole.forestId
                        }?.backUrl
                    }
                    holes
                }
                .collectLatest {
                    holesLoadState.value = LoadStatus.DONE
                    _holesV2.emit(it)
                }
        }
    }

    fun loadJoinedForestsV2() {
        viewModelScope.launch {
            repository.loadJoinedForestsV2()
                .collectLatest {
                    _forestsV2.emit(it)
                }
        }
    }

    fun refresh(hole: HoleV2) {
        viewModelScope.launch {
            val holes = _holesV2.value.toMutableList()
            val i = holes.indexOfFirst {
                it.holeId == hole.holeId
            }

            holes[i] = holes[i].copy(
                liked = hole.liked,
                likeCount = hole.likeCount,
                isReply = hole.isReply,
                replyCount = hole.replyCount,
                isFollow = hole.isFollow,
                followCount = hole.followCount
            )

            _holesV2.emit(holes)
        }
    }

    /**
     * 将小树林的图标装载到树洞列表上
     * Reason：后端返回的树洞列表不带图标Url
     * @param forests 带图标url的树林列表，默认为当前树林列表，也可以自己配置
     * @param holes 需要装载图标的树洞列表
     */
    private fun packForestAvatarUrlToHoles(
        forests: List<ForestBrief> = forestsV2.value,
        holes: List<HoleV2>
    ) {
        holes.forEach { hole ->
            hole.forestAvatarUrl = forests.find { forest ->
                forest.forestId == hole.forestId
            }?.backUrl
        }
    }

}