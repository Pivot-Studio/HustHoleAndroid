package cn.pivotstudio.modulep.hole.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.model.ReportType
import cn.pivotstudio.moduleb.rebase.network.model.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private var _reportingState = MutableStateFlow<ApiResult?>(null)
    val reportingState = _reportingState.asStateFlow()

    fun report(holeId: String, content: String?, replyId: String?, reportType: ReportType) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = HustHoleApi.retrofitService.report(
                reportRequest = RequestBody.ReportRequest(
                    holeId = holeId,
                    content = content,
                    replyId = replyId,
                    type = reportType
                )
            )

            if (response.isSuccessful) {
                _reportingState.emit(ApiResult.Success(data = Unit))
            } else {
                _reportingState.emit(
                    ApiResult.Error(
                        code = response.code(),
                        errorMessage = response.errorBody()?.string()
                    )
                )
                response.errorBody()?.close()
            }
        }
    }
}