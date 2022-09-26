package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.*
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.ABOUT
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.COMMUNITY_NORM
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.EVALUATION_AND_SUGGESTIONS
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHARE

/**
 *@classname ItemDetailFragment
 * @description:
 * @date :2022/9/26 17:51
 * @version :1.0
 * @author
 */
class ItemDetailFragment : Fragment() {
    private lateinit var binding: ViewDataBinding
    private val viewModel: MineFragmentViewModel by viewModels()
    private var option:Int = 0
    private var isVerifiedEmail:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if((activity as HomeScreenActivity).supportActionBar != null){
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        arguments?.let {
            option = it.getInt("fragType")
            isVerifiedEmail = it.getBoolean("isVerified")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        when(option) {
            R.string.campus_email -> {
                if(isVerifiedEmail) {
                    binding = ActivityEmailOkBinding.inflate(inflater,container, false)
                } else if(!isVerifiedEmail) {
                    binding = ActivityEmailVerify1Binding.inflate(inflater, container, false)
                }
            }
            R.string.privacy_security -> {
                binding = ActivitySecurityBinding.inflate(inflater, container, false)
            }
            R.string.keyword_shielding -> {
                binding = ItemLabelBinding.inflate(inflater, container, false)
            }
            COMMUNITY_NORM -> {
                binding = ActivityRulesBinding.inflate(inflater, container, false)
            }
            SHARE -> {
                binding = ActivityShareCardBinding.inflate(inflater, container, false)
            }
            EVALUATION_AND_SUGGESTIONS -> {
                binding = ActivityHoleStarBinding.inflate(inflater, container, false)
            }
            ABOUT -> {
                binding = ActivityAboutBinding.inflate(inflater, container, false)
            }
            R.string.update_log -> {
                binding = ActivityUpdateBinding.inflate(inflater, container, false)
            }
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (binding) {
            is ActivityEmailVerify1Binding -> {
                (binding as ActivityEmailVerify1Binding).apply {
                    btn1.setOnClickListener {

                    }
                    btn2.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                    emailVerify1Img.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                }
            }
            is ActivitySecurityBinding -> {
                viewModel.checkPrivacyState(binding as ActivitySecurityBinding)
                (binding as ActivitySecurityBinding).apply {
                    stSecurity.setOnCheckedChangeListener { _,isChecked ->
                        if (CheckingToken.IfTokenExist()) {
                            viewModel.changePrivacyState(!isChecked)
                        } else {
                            Toast.makeText(context, "认证信息无效，请先登录。", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            is ItemLabelBinding -> {

            }
        }
    }
}