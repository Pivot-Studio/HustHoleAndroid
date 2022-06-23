package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestResponse
import cn.pivotstudio.modulec.homescreen.model.ForestTypes
import cn.pivotstudio.modulec.homescreen.repository.AllForestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

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
