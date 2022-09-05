package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.*
import cn.pivotstudio.husthole.moduleb.network.model.ForestHoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestHead
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailHolesLoadStatus
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ForestDetailViewModel(val forestId: Int) : ViewModel() {
    val repository = ForestDetailRepository(forestId = forestId)

    val overview: LiveData<ForestCard> = repository.overview

    private var _holesV2 = MutableStateFlow<List<ForestHoleV2>>(emptyList())
    val holesV2: StateFlow<List<ForestHoleV2>> = _holesV2

    val state = repository.state
    val tip: MutableLiveData<String?> = repository.tip

    private var _loadLaterHoleId = -1

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

//    fun loadBrief() {
//        viewModelScope.launch {
//            repository.loadBriefByForestId()
//                .flowOn(Dispatchers.IO)
//                .catch { it.printStackTrace() }
//                .collect {
//                    _forestBrief.emit(it)
//                }
//        }
//    }

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

    fun checkIfJoinedTheForest(forestsJoined: List<ForestBrief>) {
        overview.value?.let { overview ->
            forestsJoined.forEach {
                if (it.forestId.toInt() == overview.forestId)
                    overview.Joined = true
            }
        }
    }

    fun giveALikeToTheHole(hole: Hole) {
        repository.giveALikeToTheHole(hole as ForestHoleV2)
    }

    fun followTheHole(hole: Hole) {
        repository.followTheHole(hole as ForestHoleV2)
        loadHoles()
    }

    fun deleteTheHole(hole: Hole) {
        repository.deleteTheHole(hole as ForestHoleV2)
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

    fun refreshLoadLaterHole(
        isThumb: Boolean,
        replied: Boolean,
        followed: Boolean,
        thumbNum: Int,
        replyNum: Int,
        followNum: Int
    ) {
        if (_loadLaterHoleId < 0) return
        val newItems = _holesV2.value.toMutableList()
        for ((i, newHole) in newItems.withIndex()) {
            if (_loadLaterHoleId == newHole.holeId.toInt())
                newItems[i] = newHole.copy().apply {
                    this.likeCount = thumbNum.toLong()
                    liked = isThumb
                    this.isReply = replied
                    this.isFollow = followed
                    this.replyCount = replyNum.toLong()
                    this.followCount = followNum.toLong()
                }
        }
        _holesV2.value = newItems

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