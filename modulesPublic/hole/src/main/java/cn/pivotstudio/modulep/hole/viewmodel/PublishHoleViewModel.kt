package cn.pivotstudio.modulep.hole.viewmodel

import androidx.lifecycle.*
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.model.ForestBrief
import cn.pivotstudio.modulep.hole.repository.PublishHoleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PublishHoleViewModel : ViewModel() {

    private val repository: PublishHoleRepository = PublishHoleRepository()
    private var _forestsMeta = listOf<ForestBrief>()
    private var _forests = MutableStateFlow<List<Pair<String, List<ForestBrief>>>>(mutableListOf())
    private var _joinedForests = MutableStateFlow<List<ForestBrief>>(mutableListOf())

    private var _loadingState = MutableStateFlow<ApiResult?>(null)
    val loadingState = _loadingState.asStateFlow()

    private var _errorTip = MutableSharedFlow<String>()
    val errorTip = _errorTip.asSharedFlow()

    //所有小树林
    @JvmField
    val typeForestList: LiveData<List<Pair<String, List<ForestBrief>>>> = _forests.asLiveData()

    //加入的小树林
    @JvmField
    var joinedForest: LiveData<List<ForestBrief>> = _joinedForests.asLiveData()

    //选择的小树林的名字
    @JvmField
    var forestName: MutableLiveData<String> = MutableLiveData()

    //选中的小树林的id
    var forestId: String? = null

    fun publishAHole(forestId: String? = this.forestId, content: String) {
        viewModelScope.launch {
            repository.publishAHole(forestId, content)
                .collectLatest { state ->
                    _loadingState.emit(state)
                }
        }
    }

    fun loadAllForests() {
        viewModelScope.launch {
            repository.loadAllForests()
                .map {
                    _forestsMeta = it
                    transToAllForestsWithType(it)
                }.collectLatest {
                    _forests.emit(it)
                }
        }
    }

    fun loadJoinedForestsV2() {
        viewModelScope.launch {
            repository.loadJoinedForestsV2()
                .collectLatest {
                    _joinedForests.emit(it)
                }
        }
    }

    private fun transToAllForestsWithType(forestList: List<ForestBrief>): List<Pair<String, List<ForestBrief>>> {
        val forests = mutableListOf<Pair<String, List<ForestBrief>>>()
        val forestsWithType = mutableListOf<ForestBrief>()
        if (forestList.isEmpty()) {
            return forests
        }

        var lastType = forestList.first().type
        forestList.forEach {
            if (lastType == it.type) {
                forestsWithType.add(it)
            } else {
                forests.add(Pair(lastType!!, forestsWithType.toList()))
                forestsWithType.clear()
                lastType = it.type
            }
        }
        forests.add(Pair(lastType!!, forestsWithType.toList()))
        return forests
    }

    init {
        loadJoinedForestsV2()
        loadAllForests()
    }
}