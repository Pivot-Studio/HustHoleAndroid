package cn.pivotstudio.modulec.homescreen.ui.fragment.lar

import android.os.Bundle
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.rebase.lib.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentSetPasswordBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.LARViewModel

class SetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSetPasswordBinding
    private lateinit var navController: NavController
    private val viewModel: LARViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_set_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            EditTextUtil.EditTextSize(etSetPassword, SpannableString("输入密码"), 14)
            EditTextUtil.ButtonReaction(etSetPassword, btnJoinIn)

            ckSetPasswordCheckBox.setOnClickListener {
                when (ckSetPasswordCheckBox.isChecked) {
                    true -> {
                        ckSetPasswordCheckBox.text = resources.getString(R.string.display_password)
                        etSetPassword.transformationMethod = null
                    }
                    false -> {
                        ckSetPasswordCheckBox.text = resources.getString(R.string.hide_password)
                        etSetPassword.transformationMethod = PasswordTransformationMethod()
                    }
                }
            }

            btnJoinIn.setOnClickListener {
                viewModel.register(etSetPassword.text.toString())
            }
        }

        viewModel.apply {

            showPasswordWarning.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) binding.tvSetPasswordWarn.visibility = View.GONE
                    else binding.tvSetPasswordWarn.visibility = View.VISIBLE
                }
            }

            lifecycleScope.launchWhenStarted {
                loginTokenV2.collect { token ->
                    token.takeIf { it.isNotBlank() }?.let {
                        (activity as? cn.pivotstudio.modulec.homescreen.ui.activity.LARActivity)?.loginWithUseToken(it)
                    }
                }
            }
        }
    }

}