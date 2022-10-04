package cn.pivotstudio.modulep.report.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.model.ReportType
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulep.report.BuildConfig
import cn.pivotstudio.modulep.report.R
import cn.pivotstudio.modulep.report.databinding.ActivityReportBinding
import cn.pivotstudio.modulep.report.viewmodel.ReportViewModel
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.githang.statusbar.StatusBarCompat

/**
 * @classname: ReportActivity
 * @description: 举报页面
 * @date: 2022/5/18 14:50
 * @version:1.0
 * @author:
 */
@Route(path = "/report/ReportActivity")
class ReportActivity : BaseActivity() {
    @JvmField
    @Autowired(name = Constant.HOLE_ID)
    var holeId = ""

    @JvmField
    @Autowired(name = Constant.REPLY_LOCAL_ID)
    var replyLocalId: String? = null

    @JvmField
    @Autowired(name = Constant.ALIAS)
    var alias: String? = null

    var reportType: ReportType? = null
    var lastClickBtn: Button? = null
    private val mViewModel: ReportViewModel by lazy { ViewModelProvider(this)[ReportViewModel::class.java] }
    private lateinit var binding: ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this) //初始化@Autowired
        initView()
    }

    private fun initView() {
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.HH_BandColor_1, null), true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        binding.tvReportHoleid.text = "#$holeId"
        binding.tvAlias.text = alias
        binding.tvTitlebargreenTitle.text = "举报"
        binding.titlebargreenAVLoadingIndicatorView.hide()
        binding.titlebargreenAVLoadingIndicatorView.visibility = View.GONE
        EditTextUtil.ButtonReaction(
            binding.etReport,
            binding.btnReport
        )

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

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_report -> {
                if (reportType != null) {
                    mViewModel.report(
                        holeId,
                        binding.etReport.text.toString(),
                        replyLocalId,
                        reportType!!
                    )
                } else {
                    showMsg("您还未选择举报类型")
                }
                closeKeyBoard()
            }
            R.id.cl_titlebargreen_back -> {
                if (BuildConfig.isRelease) {
                    finish()
                    closeKeyBoard()
                } else {
                    showMsg("当前处于模块测试阶段")
                }
            }
            R.id.button_illegal -> {
                reportType = ReportType.ILLEGAL
                chooseReportType(view as Button)
            }
            R.id.button_bloody_violence -> {
                reportType = ReportType.BLOODY_VIOLENCE
                chooseReportType(view as Button)
            }
            R.id.button_copyright_violations -> {
                reportType = ReportType.COPYRIGHT_VIOLATIONS
                chooseReportType(view as Button)
            }
            R.id.button_personal_attack -> {
                reportType = ReportType.PERSONAL_ATTACK
                chooseReportType(view as Button)
            }
            R.id.button_place_ad -> {
                reportType = ReportType.PLACE_AD
                chooseReportType(view as Button)
            }
            R.id.button_scam_gambling -> {
                reportType = ReportType.SCAM_GAMBLING
                chooseReportType(view as Button)
            }
            R.id.button_troll -> {
                reportType = ReportType.TROLL
                chooseReportType(view as Button)
            }
            R.id.button_pron -> {
                reportType = ReportType.PRON
                chooseReportType(view as Button)
            }
            R.id.button_other -> {
                reportType = ReportType.OTHER
                chooseReportType(view as Button)
            }
        }
    }


    private fun chooseReportType(btn: Button) {
        if (lastClickBtn != null) {
            lastClickBtn!!.background = AppCompatResources.getDrawable(
                this,
                R.drawable.report_button
            )
        }
        btn.background = AppCompatResources.getDrawable(
            this,
            R.drawable.report_button_green
        )
        lastClickBtn = btn
    }
}