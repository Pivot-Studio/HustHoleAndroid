package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cn.pivotstudio.modulec.homescreen.model.ForestHeads
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.repository.ForestRepository

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
class ForestViewModel : ViewModel() {
    private val repository = ForestRepository()

    val forestHoles: LiveData<List<ForestHole>> = repository.forestHoles
    val forestHeads: LiveData<ForestHeads> = repository.forestHeads

    init {
        loadHolesAndHeads()
    }

    fun loadHolesAndHeads() {
        repository.run {
            loadForestHoles()
            loadForestHeads()
        }
    }



}