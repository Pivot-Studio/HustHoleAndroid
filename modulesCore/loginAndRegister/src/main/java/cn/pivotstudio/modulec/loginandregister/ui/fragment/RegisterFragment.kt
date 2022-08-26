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
import cn.pivotstudio.modulec.loginandregister.databinding.FragmentRegisterBinding
import cn.pivotstudio.modulec.loginandregister.viewmodel.LARViewModel

class RegisterFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val navController by lazy { findNavController() }
    private val viewModel: LARViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvRegisterHadRegistered.setOnClickListener {
                navController.popBackStack()
            }

            tvRegisterPrivacy.setOnClickListener {
                navController.navigate(R.id.action_registerFragment_to_privacyFragment)
            }

            EditTextUtil.EditTextSize(
                etRegisterStudentCode,
                SpannableString(resources.getString(R.string.login_id_2)), 14
            )
            EditTextUtil.ButtonReaction(etRegisterStudentCode, btnNext)

            btnNext.setOnClickListener {
                viewModel.studentCode.value = etRegisterStudentCode.text.toString()
                viewModel.isResetPassword = false
                viewModel.sendVerifyCodeToStudentEmail()
            }
        }

        viewModel.apply {
            showStudentCodeWarning.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) binding.tvRegisterWarn.visibility = View.VISIBLE
                    else {
                        binding.tvRegisterWarn.visibility = View.GONE
                        doneShowingWarning()
                        navToVerifyCode()
                    }
                }
            }

            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    viewModel.doneShowingTip()
                }
            }
        }
    }

    private fun navToVerifyCode() {
        navController.navigate(R.id.action_registerFragment_to_verifyCodeFragment)
    }
}