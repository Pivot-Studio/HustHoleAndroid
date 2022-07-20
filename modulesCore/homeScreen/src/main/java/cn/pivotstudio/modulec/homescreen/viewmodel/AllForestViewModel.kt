package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import cn.pivotstudio.modulec.homescreen.repository.AllForestRepository

class AllForestViewModel : ViewModel() {

    // model
    private val repository = AllForestRepository()

    val forestCardsWithOneType = repository.forestCardWithOneType
    val forestCards = repository.forestCards


    init {
        loadAllTypeOfForestCards()
    }

    fun loadAllTypeOfForestCards() {
        repository.loadAllTypeOfForestCards()
    }
}
