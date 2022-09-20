package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.ForestHole
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

    private var _loadLaterHoleId = -1

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
        loadHolesV2()
        loadJoinedForestsV2()
    }

    fun loadMoreForestHoles() {
        holesLoadState.value = LoadStatus.LOADING
        viewModelScope.launch {
            repository.loadMoreForestHolesV2()
                .collectLatest {
                    val newHoles = _holesV2.value.toMutableList() + it
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
                .collectLatest {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            refreshTheHole(hole)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun followTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.followTheHole(hole)
                .collectLatest {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            refreshTheHole(hole)
                        }
                        is ApiResult.Error -> {
                            tip.value = it.code.toString() + it.errorMessage
                        }
                        else -> {}
                    }
                }
        }
    }

    fun refreshTheHole(hole: HoleV2) {
        viewModelScope.launch {
            repository.loadTheHole(hole)
                .collectLatest {
                    val newItems = _holesV2.value.toMutableList()
                    for ((i, newHole) in newItems.withIndex()) {
                        if (hole.holeId == newHole.holeId) newItems[i] = newItems[i].copy(followCount = 100, isFollow = true)
                    }
                    _holesV2.emit(newItems)
                }
        }
    }

    fun deleteTheHole(hole: ForestHole) {
        repository.deleteTheHole(hole)
    }

    fun doneShowingTip() {
        tip.value = null
    }

    fun showPlaceHolder() {
        repository.holeState.value = null
    }

    fun loadHoleLater(holeId: Int) {
        _loadLaterHoleId = holeId
    }

    fun refreshLoadLaterHole(
        isThumb: Boolean?,
        replied: Boolean?,
        followed: Boolean?,
        thumbNum: Int?,
        replyNum: Int?,
        followNum: Int?
    ) {
        if (isThumb == null) return
        if (replied == null) return
        if (followed == null) return
        if (thumbNum == null) return
        if (replyNum == null) return
        if (followNum == null) return

        repository.refreshLoadLaterHole(
            _loadLaterHoleId,
            isThumb,
            replied,
            followed,
            thumbNum,
            replyNum,
            followNum
        )
    }

    fun loadHolesV2() {
        viewModelScope.launch {
            repository.loadForestHolesV2()
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

}