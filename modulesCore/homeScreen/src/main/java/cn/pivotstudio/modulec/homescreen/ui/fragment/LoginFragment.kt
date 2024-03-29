package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.rebase.database.MMKVUtil
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.rebase.lib.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentLoginBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.LARViewModel


class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LARViewModel by activityViewModels()
    private val navController by lazy { findNavController() }
    private lateinit var mmkvUtil: MMKVUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        mmkvUtil = MMKVUtil.getMMKV(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvHadNotRegistered.setOnClickListener {
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
            }

            tvForgetPassword.setOnClickListener {
                navController.navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
            }

            btnLogin.setOnClickListener {
                viewModel.login(etLoginStudentCode.text.toString(), etLoginPassword.text.toString())
            }

            //Et与Bt绑定，动态改变button背景
            EditTextUtil.EditTextSize(
                etLoginStudentCode,
                SpannableString(resources.getString(R.string.register_2)), 14
            )
            EditTextUtil.EditTextSize(
                etLoginPassword,
                SpannableString(resources.getString(R.string.register_4)), 14
            )
            EditTextUtil.ButtonReaction(etLoginStudentCode, btnLogin)

        }

        viewModel.apply {
            lifecycleScope.launchWhenStarted {
                loginTokenV2.collect { token ->
                    token.takeIf { it.isNotBlank() }?.let {
                        (requireActivity() as cn.pivotstudio.modulec.homescreen.ui.activity.LARActivity).loginWithUseToken(it)
                    }
                }
            }

            showStudentCodeWarning.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) binding.tvLoginWarn.visibility = View.VISIBLE
                    else {
                        binding.tvLoginWarn.visibility = View.GONE
                        doneShowingWarning()
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
}