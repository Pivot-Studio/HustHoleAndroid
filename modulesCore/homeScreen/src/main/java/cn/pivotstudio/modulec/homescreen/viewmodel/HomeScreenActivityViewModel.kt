package cn.pivotstudio.modulec.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.model.VersionInfo
import cn.pivotstudio.modulec.homescreen.repository.HomeScreenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenActivityViewModel : ViewModel() {
    val repository = HomeScreenRepository()
    val tip: MutableLiveData<String?> = repository.tip
    private val _versionInfo = MutableStateFlow<VersionInfo?>(null)

    val versionInfo = _versionInfo.asStateFlow()

    init {
        getVersionInfo()
    }

    private fun getVersionInfo() {
        viewModelScope.launch {
            repository.getVersion().collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        _versionInfo.emit(it.data as VersionInfo)
                    }
                    is ApiResult.Error -> {
                        _versionInfo.emit(VersionInfo(it.code.toString(), it.errorMessage.toString(), "", ""))
                        tip.value = it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun doneShowingTip() {
        repository.tip.value = null
    }
}