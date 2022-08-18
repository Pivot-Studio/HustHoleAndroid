package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.*
import cn.pivotstudio.husthole.moduleb.network.model.DetailForestHole
import cn.pivotstudio.husthole.moduleb.network.model.DetailForestHoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestHead
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailHolesLoadStatus
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ForestDetailViewModel(val forestId: Int) : ViewModel() {
    val repository = ForestDetailRepository(forestId = forestId)

    private var _forestBrief = MutableStateFlow(ForestBrief())
    val forestBrief = _forestBrief

    val overview: LiveData<ForestCard> = repository.overview

    private var _holesV2 = MutableStateFlow<List<DetailForestHoleV2>>(emptyList())
    val holesV2: StateFlow<List<DetailForestHoleV2>> = _holesV2

    val state = repository.state
    val tip: MutableLiveData<String?> = repository.tip

    private var _shouldRefreshHoles = false

    fun loadHolesLater() {
        _shouldRefreshHoles = true
    }

    /**
     * 从其他页面回来想要刷新
     *  1. loadxxxLater()
     *  2. tryLoadNewxxx() in View
     */
    fun tryLoadNewHoles() {
        if (_shouldRefreshHoles) {
            viewModelScope.launch { loadHoles() }
            _shouldRefreshHoles = false
        }
    }

    init {
        loadHoles()
        loadOverview()
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

    fun loadOverview() {
        repository.loadOverviewByForestId(id = forestId)
    }

    fun loadBrief() {
        viewModelScope.launch {
            repository.loadBriefByForestId()
                .flowOn(Dispatchers.IO)
                .catch { it.printStackTrace() }
                .collect {
                    _forestBrief.emit(it)
                }
        }
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

    fun checkIfJoinedTheForest(forestsJoined: List<ForestHead>) {
        overview.value?.let { overview ->
            forestsJoined.forEach {
                if (it.forestId == overview.forestId)
                    overview.Joined = true
            }
        }
    }

    fun giveALikeToTheHole(hole: Hole) {
        repository.giveALikeToTheHole(hole as DetailForestHoleV2)
    }

    fun followTheHole(hole: Hole) {
        repository.followTheHole(hole as DetailForestHoleV2)
    }

    fun deleteTheHole(hole: Hole) {
        repository.deleteTheHole(hole as DetailForestHoleV2)
    }

    fun joinTheForest() {
        repository.joinTheForest(forestId)
    }

    fun quitTheForest() {
        repository.quitTheForest(forestId)
    }

    fun doneShowingTip() {
        tip.value = null
    }

}

class ForestDetailViewModelFactory(private val forestId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForestDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForestDetailViewModel(forestId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}