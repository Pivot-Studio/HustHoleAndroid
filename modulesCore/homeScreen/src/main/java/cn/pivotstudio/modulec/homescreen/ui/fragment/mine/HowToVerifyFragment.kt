package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import cn.pivotstudio.modulec.homescreen.databinding.ActivityEmailHowToBinding
import cn.pivotstudio.modulec.homescreen.databinding.ActivityEmailVerify2Binding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity

/**
 *@classname HowToVerifyFragment
 * @description:
 * @date :2022/10/1 22:50
 * @version :1.0
 * @author
 */
class HowToVerifyFragment : Fragment() {
    private lateinit var binding: ActivityEmailHowToBinding

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
        binding = ActivityEmailHowToBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.useEmailImg.setOnClickListener{
            view.findNavController().popBackStack()
        }
    }
}