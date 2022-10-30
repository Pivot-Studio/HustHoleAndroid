package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentEvaluateBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import com.google.android.material.chip.Chip


/**
 *@classname EvaluateFragment
 * @description:
 * @date :2022/9/29 16:41
 * @version :1.0
 * @author
 */
class EvaluateFragment : Fragment() {
    private lateinit var binding: FragmentEvaluateBinding
    private val viewModel: MineFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEvaluateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        for (i in 1..10) {
            binding.chipGroup.addView(createSingleChip(i))
        }
        binding.ok.isClickable = false
        binding.chipGroup.setOnCheckedStateChangeListener { _, _ ->
            if (binding.chipGroup.checkedChipId == View.NO_ID) {
                binding.ok.setBackgroundResource(R.drawable.standard_button_gray)
                binding.ok.setTextColor(resources.getColor(R.color.GrayScale_20, null))
                binding.ok.isClickable = false
            } else {
                binding.ok.setBackgroundResource(R.drawable.button)
                binding.ok.setTextColor(resources.getColor(R.color.GrayScale_100, null))
                binding.ok.isClickable = true
            }
        }
        binding.ok.setOnClickListener {
            viewModel.sendEvaluation(
                binding.chipGroup[binding.chipGroup.checkedChipId - 1].id,
                binding
            )
//            Log.d("HAHA",binding.chipGroup.checkedChipId.toString()) //checkedChipId比实际多出两个数字
        }
    }

    private fun createSingleChip(
        score: Int
    ): Chip {
        val chip = layoutInflater.inflate(R.layout.item_chip, binding.chipGroup, false) as Chip
        chip.id = score
        chip.text = score.toString()
        return chip
    }

    companion object {
        const val TAG = "EvaluateFragment"

        @JvmStatic
        fun newInstance(): EvaluateFragment {
            return EvaluateFragment()
        }
    }
}