package cn.pivotstudio.modulec.homescreen.ui.fragment.lar

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.rebase.lib.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForgetPasswordBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.LARViewModel


class ForgetPasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private val navController by lazy { findNavController() }
    private val viewModel: LARViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            EditTextUtil.EditTextSize(
                etForgetEmail,
                SpannableString(resources.getString(R.string.login_id_2)), 14
            )
            EditTextUtil.ButtonReaction(etForgetEmail, btnForgetGetCode)

            btnForgetGetCode.setOnClickListener {
                viewModel.studentCode.value = etForgetEmail.text.toString()
                viewModel.isResetPassword = true
                viewModel.sendVerifyCodeToStudentEmail()
            }

            tvForgetAppeal.setOnClickListener {
                Toast.makeText(context, getString(R.string.lar_appeal_to), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    viewModel.doneShowingTip()
                }
            }

            showStudentCodeWarning.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) binding.tvForgetWarn.visibility = View.VISIBLE
                    else {
                        binding.tvForgetWarn.visibility = View.GONE
                        navController.navigate(R.id.action_forgetPasswordFragment_to_verifyCodeFragment)
                        doneShowingWarning()
                    }
                }
            }
        }


    }
}