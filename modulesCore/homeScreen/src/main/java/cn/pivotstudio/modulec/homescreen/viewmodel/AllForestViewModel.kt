package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.modulec.homescreen.repository.AllForestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AllForestViewModel : ViewModel() {

    // model
    private val repository = AllForestRepository()

    val forestCards = repository.forestCards
    val loadState = repository.loadState

    private var _forestsMeta = listOf<ForestBrief>()
    private var _forests = MutableStateFlow<List<Pair<String, List<ForestBrief>>>>(mutableListOf())
    val forests: StateFlow<List<Pair<String, List<ForestBrief>>>> = _forests

    fun doneShowingPlaceHolder() {
        loadState.value = null
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

    fun preload() {
        if (forestCards.isEmpty()) {
            viewModelScope.launch {
                loadAllForests()
            }
        }
    }

    fun getForestById(forestId: String): ForestBrief? = _forestsMeta.find {
        it.forestId == forestId
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
}
