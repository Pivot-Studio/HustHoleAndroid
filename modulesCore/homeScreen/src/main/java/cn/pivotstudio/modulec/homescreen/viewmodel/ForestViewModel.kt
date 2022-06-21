package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.model.ForestResponse.ForestHead
import cn.pivotstudio.modulec.homescreen.repository.ForestRepository

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
class ForestViewModel : ViewModel() {
    private val repository: ForestRepository = ForestRepository()

    val forestHoles: LiveData<List<ForestHole>> = repository.forestHoles
    val forestHeads = MutableLiveData<List<ForestHead>>()

    init {
        repository.loadForestHoles()
    }

}