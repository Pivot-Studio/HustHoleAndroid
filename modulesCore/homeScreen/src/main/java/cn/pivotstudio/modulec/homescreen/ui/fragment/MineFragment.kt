package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineOthersAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.OTHER_OPTION
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel

/**
 * 设置加入天数的显示颜色
 */
@BindingAdapter("joinDay")
fun bindDay(
    view: TextView,
    text: String
) {
    val ss = SpannableString(text)
    ss.setSpan(
        ForegroundColorSpan(Color.parseColor("#9966CC")),
        7,
        text.lastIndexOf("天"),
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    view.text = ss
}

class MineFragment : BaseFragment() {
    private lateinit var binding: FragmentMineBinding
    private val viewModel: MineFragmentViewModel by viewModels()
    private lateinit var action: NavDirections

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            myHole.setOnClickListener {
                action = MineFragmentDirections.actionMineFragmentToHoleFollowReplyFragment(
                    MyHoleFragmentViewModel.GET_HOLE
                )
                view.findNavController().navigate(action)
            }
            myStar.setOnClickListener {
                action = MineFragmentDirections.actionMineFragmentToHoleFollowReplyFragment(
                    MyHoleFragmentViewModel.GET_FOLLOW
                )
                view.findNavController().navigate(action)
            }
            myReply.setOnClickListener {
                action = MineFragmentDirections.actionMineFragmentToHoleFollowReplyFragment(
                    MyHoleFragmentViewModel.GET_REPLY
                )
                view.findNavController().navigate(action)
            }
        }

        val adapter = MineOthersAdapter(OTHER_OPTION, viewModel, this)
        viewModel.myNameList.observe(viewLifecycleOwner) { list -> adapter.submitList(list) }
        binding.rvOptions.apply {
            this.adapter = adapter
            addItemDecoration(SpaceItemDecoration(0, 2))
        }
    }

    override fun onResume() {
        viewModel.getMineData()
        super.onResume()
    }

    class SpaceItemDecoration(
        private val leftRight: Int,
        private val topBottom: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                //最后一项需要 bottom
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    outRect.bottom = topBottom
                }
                if(parent.getChildAdapterPosition(view) == 4 || parent.getChildAdapterPosition(view) == 7) {
                    outRect.top = topBottom + 10
                } else {
                    outRect.top = topBottom
                }
                outRect.left = leftRight
                outRect.right = leftRight
            } else {
                //最后一项需要right
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    outRect.right = leftRight
                }
                outRect.top = topBottom
                outRect.left = leftRight
                outRect.bottom = topBottom
            }
        }
    }
}