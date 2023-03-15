package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import cn.pivotstudio.modulec.homescreen.databinding.ActivityEmailVerify2Binding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel

/**
 *@classname VerifyFragment
 * @description:
 * @date :2022/10/1 22:13
 * @version :1.0
 * @author
 */
class VerifyFragment : Fragment() {
    private lateinit var binding: ActivityEmailVerify2Binding
    private val viewModel: MineFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityEmailVerify2Binding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            llEmailverify2.visibility = View.INVISIBLE
            btnVerify.setOnClickListener{
                viewModel.sendEmailVerify(this)
            }
            tvAgain.setOnClickListener{
                viewModel.sendEmailVerifyAgain(this)
            }
            emailVerify2Img.setOnClickListener{
                view.findNavController().popBackStack()
            }
        }
    }

    override fun onResume() {
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onResume()
    }
}