package cn.pivotstudio.modulep.hole.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.model.ReportType
import cn.pivotstudio.moduleb.rebase.lib.base.ui.activity.BaseActivity
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.rebase.lib.util.ui.SoftKeyBoardUtil
import cn.pivotstudio.moduleb.rebase.theme.HustHoleTheme
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.databinding.ActivityReportBinding
import cn.pivotstudio.modulep.hole.viewmodel.ReportViewModel
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = "/hole/ReportActivity")
class ReportActivity : BaseActivity() {
    @JvmField
    @Autowired(name = Constant.HOLE_ID)
    var holeId = ""

    @JvmField
    @Autowired(name = Constant.REPLY_ID)
    var replyId: String? = null

    @JvmField
    @Autowired(name = Constant.ALIAS)
    var alias: String? = null

    var reportType: ReportType? = null
    var lastClickBtn: Button? = null
    private val mViewModel: ReportViewModel by lazy { ViewModelProvider(this)[ReportViewModel::class.java] }
    private lateinit var binding: ActivityReportBinding

    private var feedbackContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this) //初始化@Autowired
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        binding.composeReportScreen.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HustHoleTheme {
                    ReportScreen(holeId = holeId,
                        targetNickname = alias ?: "洞主",
                        involvedTypes = listOf(
                            stringResource(id = R.string.report_BloodyViolence),
                            stringResource(id = R.string.report_CopyrightViolations),
                            stringResource(id = R.string.report_Illegal),
                            stringResource(id = R.string.report_Other),
                            stringResource(id = R.string.report_PersonalAttack),
                            stringResource(id = R.string.report_PlaceAd),
                            stringResource(id = R.string.report_Pron),
                            stringResource(id = R.string.report_ScamGambling),
                            stringResource(id = R.string.report_Troll)
                        ),
                        makeSure2Report = {
                            if (reportType != null) {
                                mViewModel.report(
                                    holeId = holeId,
                                    content = feedbackContent,
                                    replyId = replyId,
                                    reportType = reportType!!
                                )
                            } else {
                                showMsg("您还未选择举报类型")
                            }
                            SoftKeyBoardUtil.hideKeyboard(this@ReportActivity)
                        })
                }
            }
        }

        mViewModel.apply {
            lifecycleScope.launchWhenStarted {
                reportingState.collect { state ->
                    state?.let {
                        when (it) {
                            is ApiResult.Success<*> -> {
                                showMsg(getString(R.string.report_successful))
                                finish()
                            }

                            is ApiResult.Error -> {
                                showMsg(getString(R.string.report_failed, it.errorMessage))
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

    }
}
