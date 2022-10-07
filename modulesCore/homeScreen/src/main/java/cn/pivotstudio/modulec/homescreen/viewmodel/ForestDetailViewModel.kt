package cn.pivotstudio.modulec.homescreen.viewmodel;

import androidx.lifecycle.*
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailHolesLoadStatus
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailRepository
import kotlinx.coroutines.flow.*
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

    private var _loadLaterHoleId = -1

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

    fun giveALikeToTheHole(hole: Hole) {
        repository.giveALikeToTheHole(hole as HoleV2)
    }

    fun followTheHole(hole: Hole) {
        repository.followTheHole(hole as HoleV2)
        loadHoles()
    }

    fun deleteTheHole(hole: Hole) {
        repository.deleteTheHole(hole as HoleV2)
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