package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestResponse
import cn.pivotstudio.modulec.homescreen.repository.AllForestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class AllForestViewModel : ViewModel() {

    // model
    private val repository = AllForestRepository()

    val forestTypes: LiveData<List<String>> = repository.forestTypes
    val forestCards: LiveData<List<ForestCard>> = repository.forestCards

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadAllTypeOfForestCards()
        }
    }

    fun loadAllTypeOfForestCards() {
        repository.loadForestTypesloadAllTypeOfForestCards()
    }
}
