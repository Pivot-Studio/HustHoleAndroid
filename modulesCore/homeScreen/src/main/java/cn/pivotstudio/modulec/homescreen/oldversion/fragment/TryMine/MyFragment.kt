package cn.pivotstudio.modulec.homescreen.oldversion.fragment.TryMine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MyFragment : Fragment(){
    private val myViewModel by activityViewModels<MyViewModel>()

    fun newInstance() = MyFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = cn.pivotstudio.modulec.homescreen.databinding.FragmentMyBinding
            .inflate(inflater, container, false)
            .apply {
                my = myViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }

}