package cn.pivotstudio.modulec.homescreen.ui.fragment.lar

import android.os.Bundle
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentSetNewPasswordBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.LARViewModel


class SetNewPasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentSetNewPasswordBinding
    private lateinit var navController: NavController
    private val viewModel: LARViewModel by activityViewModels()
    private lateinit var mmkvUtil: MMKVUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_set_new_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        navController = findNavController()
        mmkvUtil = MMKVUtil.getMMKV(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var hide = false
        binding.apply {
            EditTextUtil.EditTextSize(etSetNewPassword, SpannableString("输入密码"), 14)
            EditTextUtil.ButtonReaction(etSetNewPassword, btnSetNewPassword)

            ivHidePassword.setOnClickListener {
                if (hide) {
                    ivHidePassword.setImageResource(R.drawable.checkbox_false)
                    etSetNewPassword.transformationMethod = PasswordTransformationMethod()
                    hide = false
                } else {
                    ivHidePassword.setImageResource(R.drawable.checkbox_true)
                    etSetNewPassword.transformationMethod = null
                    hide = true
                }
            }

            btnSetNewPassword.setOnClickListener {
                viewModel.setNewPassword(etSetNewPassword.text.toString())
            }

        }


        viewModel.apply {

            lifecycleScope.launchWhenStarted {
                loginTokenV2.collect { token ->
                    token.takeIf { it.isNotBlank() }?.let {
                        (activity as? cn.pivotstudio.modulec.homescreen.ui.activity.LARActivity)?.loginWithUseToken(it)
                    }
                }
            }

            showPasswordWarning.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) binding.tvModifyWarn.visibility = View.GONE
                    else binding.tvModifyWarn.visibility = View.VISIBLE
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