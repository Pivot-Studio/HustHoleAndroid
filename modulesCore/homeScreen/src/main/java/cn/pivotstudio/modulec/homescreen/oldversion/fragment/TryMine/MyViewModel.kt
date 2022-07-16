package cn.pivotstudio.modulec.homescreen.oldversion.fragment.TryMine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyViewModel : ViewModel(){
    var myData: MyData = MyData()
    private val repository: RepositoryImpl = RepositoryImpl.getInstance()

    init {
        viewModelScope.launch {
            async { myData = repository.getMyData()}
        }
    }

}