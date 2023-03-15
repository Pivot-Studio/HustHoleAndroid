package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineOthersAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel
import com.alibaba.android.arouter.launcher.ARouter

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

        val adapter = MineOthersAdapter()
        viewModel.myNameList.observe(viewLifecycleOwner) { list -> adapter.submitList(list) }
        adapter.setOnItemClickListener(object :MineOthersAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int, nameID: Int) {
                if (viewModel.optSwitch[position] == true) {
                    when (position) {
                        MineFragmentViewModel.PERSONAL_SETTING, MineFragmentViewModel.SHIELD_SETTING, MineFragmentViewModel.UPDATE -> {
                            val action =
                                MineFragmentDirections.actionMineFragmentToItemMineFragment(
                                    position
                                )
                            view.findNavController().navigate(action)
                        }
                        MineFragmentViewModel.SHARE -> {
                            initShareCardView()
                        }
                        MineFragmentViewModel.LOGOUT -> {
                            initLogOutDialog()
                        }
                        else -> {
                            val action =
                                MineFragmentDirections.actionMineFragmentToItemDetailFragment2(position)
                            this@MineFragment.findNavController().navigate(action)
                        }
                    }
                } else {
                    Toast.makeText(context,"功能正在维护！", Toast.LENGTH_SHORT).show()
                }
            }
        })
        binding.rvOptions.apply {
            this.adapter = adapter
            addItemDecoration(SpaceItemDecoration(0, 2))
        }
    }

    override fun onResume() {
        viewModel.getMineData()
        super.onResume()
    }

    private fun cancelDarkBackGround() {
        val lp = this.requireActivity().window.attributes
        lp.alpha = 1f // 0.0~1.0
        this.requireActivity().window.attributes = lp
    }   //取消暗背景

    private fun initShareCardView() {
        val shareCardView = View.inflate(context, R.layout.ppw_share, null)
        val shareCard = shareCardView.findViewById<LinearLayout>(R.id.share_card)
        val cancel = shareCardView.findViewById<TextView>(R.id.share_cancel_button)
        val ppwShare = PopupWindow(shareCardView)
        val window = this.requireActivity().window

        ppwShare.isOutsideTouchable = true  //点击卡片外部退出
        ppwShare.isFocusable = true     //按返回键允许退出
        ppwShare.width = ViewGroup.LayoutParams.MATCH_PARENT
        ppwShare.height = ViewGroup.LayoutParams.WRAP_CONTENT
        ppwShare.animationStyle = R.style.Page2Anim

        //减弱背景亮度
        window.attributes.alpha = 0.6f
        window.setWindowAnimations(R.style.darkScreenAnim)
        ppwShare.showAtLocation(
            window.decorView, Gravity.BOTTOM,
            0, 0
        )    //设置显示位置
        ppwShare.setOnDismissListener {
            cancelDarkBackGround()
        }
        cancel.setOnClickListener {
            ppwShare.dismiss()
        }
        shareCard.setOnClickListener {
            ppwShare.dismiss()
            val action =
                MineFragmentDirections.actionMineFragmentToItemDetailFragment2(MineFragmentViewModel.SHARE)
            this.findNavController().navigate(action)
        }
    }

    @SuppressLint("InflateParams")
    private fun initLogOutDialog() {
        val dialog = Dialog(this.requireContext())
        val dialogView = this.requireActivity().layoutInflater.inflate(R.layout.dialog_logout, null)
        dialog.setContentView(dialogView)
        val btnCancel = dialogView.findViewById<Button>(R.id.cancel)
        val btnLogout = dialogView.findViewById<Button>(R.id.logout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnLogout.setOnClickListener {
            dialog.dismiss()
            val mmkvUtil = MMKVUtil.getMMKV(context)
            mmkvUtil.put(Constant.USER_TOKEN, "")
            mmkvUtil.put(Constant.USER_TOKEN_V2, "")
            mmkvUtil.put(Constant.IS_LOGIN, false)
            ARouter.getInstance().build("/loginAndRegister/LARActivity").navigation()
            this.requireActivity().finish()
        }
        dialog.show()
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