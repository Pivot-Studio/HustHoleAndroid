package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.modulec.homescreen.repository.AllForestRepository
import kotlinx.coroutines.launch

class AllForestViewModel : ViewModel() {

    // model
    private val repository = AllForestRepository()

    val forestCardsWithOneType = repository.forestCardWithOneType
    val forestCards = repository.forestCards
    val loadState = repository.loadState

    fun loadAllTypeOfForestCards() {
        repository.loadAllTypeOfForestCards()
    }

    fun doneShowingPlaceHolder() {
        loadState.value = null
    }

    fun preload() {
        if (forestCards.isEmpty()) {
            viewModelScope.launch {
                loadAllTypeOfForestCards()
            }
        }
    }
}
