package cn.pivotstudio.modulec.loginandregister.ui.fragment

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.loginandregister.R
import cn.pivotstudio.modulec.loginandregister.databinding.FragmentVerifyCodeBinding
import cn.pivotstudio.modulec.loginandregister.viewmodel.LARState
import cn.pivotstudio.modulec.loginandregister.viewmodel.LARViewModel

class VerifyCodeFragment : BaseFragment() {
    private lateinit var binding: FragmentVerifyCodeBinding
    private val navController by lazy { findNavController() }
    private val viewModel: LARViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_code, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.verifyViewModel = viewModel

        binding.apply {
            EditTextUtil.EditTextSize(
                etVerifyCode,
                SpannableString(resources.getString(R.string.retrieve_password_vcode_1)), 14
            )
            EditTextUtil.ButtonReaction(etVerifyCode, btnVerifyNext)

            btnVerifyNext.setOnClickListener {
                viewModel.verify(etVerifyCode.text.toString())
            }

            tvResendCode.setOnClickListener {
                tvCountDownSecond.visibility = View.VISIBLE
                tvResendCode.visibility = View.GONE
                viewModel.sendVerifyCodeToStudentEmail()
            }

        }

        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    viewModel.doneShowingTip()
                }
            }

            larState.observe(viewLifecycleOwner) {
                it?.let {
                    when (it) {
                        LARState.VERIFIED -> {
                            navToNext()
                        }
                        LARState.COUNT_DOWN_START -> {
                            binding.apply {
                                tvResendCode.visibility = View.INVISIBLE
                                layoutResendCountDown.visibility = View.VISIBLE
                            }
                        }
                        LARState.COUNT_DOWN_END -> {
                            binding.apply {
                                tvResendCode.visibility = View.VISIBLE
                                layoutResendCountDown.visibility = View.INVISIBLE
                            }
                        }
                        else -> {}
                    }
                    doneStateChanged()
                }
            }

            countDownTime.observe(viewLifecycleOwner) {
                binding.tvCountDownSecond.text = resources.getString(R.string.second ,it)
            }
        }
    }

    private fun navToNext() {
        when (viewModel.isResetPassword) {
            false -> navController.navigate(R.id.action_verifyCodeFragment_to_setPasswordFragment)
            true -> navController.navigate(R.id.action_verifyCodeFragment_to_setNewPasswordFragment)
        }

    }
}