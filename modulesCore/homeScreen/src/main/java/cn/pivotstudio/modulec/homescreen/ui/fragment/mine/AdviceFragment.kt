package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentAdviceBinding
import cn.pivotstudio.modulec.homescreen.oldversion.model.SoftKeyBoardListener
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 *@classname AdviceFragment
 * @description:
 * @date :2022/9/29 23:34
 * @version :1.0
 * @author
 */
class AdviceFragment : Fragment() {
    private lateinit var binding: FragmentAdviceBinding
    private val viewModel: MineFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdviceBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
//        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
//            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
//            view.setPadding(0, 0, 0, imeHeight)
//            insets
//        }
        binding.tvAdviceTextnumber.text = getString(R.string.charNum).format(0)
        SoftKeyBoardListener.setListener(activity,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    binding.etAdvice1.height = 600
                    binding.tvAdviceTextnumber.setPadding(0, 0, 0, 700)
                }

                override fun keyBoardHide(height: Int) {
                    binding.etAdvice1.height = 900
                    binding.tvAdviceTextnumber.setPadding(0, 0, 0, 0)
                }
            })
        for (i in 0 until viewModel.chipTitleList.value!!.size) {
            binding.chipGroup2.addView(createSingleChip(getString(viewModel.chipTitleList.value!![i]), i))
        }
        binding.btnOk.isClickable = false
        binding.chipGroup2.setOnCheckedStateChangeListener { _, _ ->
            val text = binding.etAdvice1.text
            binding.btnOk.apply {
                isClickable =
                    if (text.isNotEmpty() && binding.chipGroup2.checkedChipId != View.NO_ID) {
                        setBackgroundResource(R.drawable.button)
                        setTextColor(resources.getColor(R.color.GrayScale_100, null))
                        true
                    } else {
                        setBackgroundResource(R.drawable.standard_button_gray)
                        setTextColor(resources.getColor(R.color.GrayScale_20, null))
                        false
                    }
                Log.d("Hello", binding.chipGroup2.checkedChipId.toString())
            }
        }
        binding.btnOk.setOnClickListener {
            viewModel.sendAdvice(
                binding.chipGroup2[binding.chipGroup2.checkedChipId].id,
                binding.etAdvice1.text.toString().replace("\n", "%0A"),
                binding
            )
        }
        binding.etAdvice1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                binding.tvAdviceTextnumber.text =
                    getString(R.string.charNum).format(binding.etAdvice1.text.length)
                if (binding.etAdvice1.text.toString().length >= 300) {
                    Toast.makeText(context, "输入内容过长！", Toast.LENGTH_SHORT).show()
                }
                binding.btnOk.apply {
                    isClickable =
                        if (binding.etAdvice1.text.isNotEmpty() && binding.chipGroup2.checkedChipId != View.NO_ID) {
                            setBackgroundResource(R.drawable.button)
                            setTextColor(resources.getColor(R.color.GrayScale_100, null))
                            true
                        } else {
                            setBackgroundResource(R.drawable.standard_button_gray)
                            setTextColor(resources.getColor(R.color.GrayScale_20, null))
                            false
                        }
                }
                //隐藏软键盘
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    binding.etAdvice1.height = 300
                } else {
                    binding.etAdvice1.height = 500
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun createSingleChip(
        title: String,
        order: Int
    ): Chip {
        val chip =
            layoutInflater.inflate(R.layout.item_advice_chip, binding.chipGroup2, false) as Chip
        chip.id = order
        chip.text = title
        return chip
    }

    companion object {
        const val TAG = "AdviceFragment"

        @JvmStatic
        fun newInstance(): AdviceFragment {
            return AdviceFragment()
        }
    }
}