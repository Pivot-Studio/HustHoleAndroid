package cn.pivotstudio.modulec.loginandregister.ui.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.loginandregister.R
import cn.pivotstudio.modulec.loginandregister.databinding.FragmentSetNewPasswordBinding
import cn.pivotstudio.modulec.loginandregister.viewmodel.LARState
import cn.pivotstudio.modulec.loginandregister.viewmodel.LARViewModel

class SetNewPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSetNewPasswordBinding
    private lateinit var navController: NavController
    private val viewModel: LARViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_set_new_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        navController = findNavController()
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
            larState.observe(viewLifecycleOwner) {
                it?.let {
                    when (it) {
                        LARState.REGISTERED -> {
                            popBackWithSuccessfulRegistry()
                        }
                        else -> {}
                    }
                    doneStateChanged()
                }
            }

            showPasswordWarning.observe(viewLifecycleOwner) {
                it?.let {
                    if(it) binding.tvModifyWarn.visibility = View.GONE
                    else binding.tvModifyWarn.visibility = View.VISIBLE
                }
            }
        }

    }

    fun popBackWithSuccessfulRegistry() {
        viewModel.clear()
        navController.popBackStack(R.id.welcomeFragment, false)
    }
}