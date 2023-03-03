package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineOthersBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.ui.fragment.MineFragment
import cn.pivotstudio.modulec.homescreen.ui.fragment.MineFragmentDirections
import cn.pivotstudio.modulec.homescreen.ui.fragment.mine.ItemMineFragment
import cn.pivotstudio.modulec.homescreen.ui.fragment.mine.ItemMineFragmentDirections
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.DETAIL
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.LOGOUT
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.OTHER_OPTION
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.PERSONAL_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHARE
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHIELD_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.UPDATE
import com.alibaba.android.arouter.launcher.ARouter

/**
 *@classname MineOtherAdapter
 * @description: 选项RecycleView适配器
 * @date :2022/9/23 22:35
 * @version :1.0
 * @author
 */
class MineOthersAdapter(
    private val type: Int,
    private val viewModel: MineFragmentViewModel,
    private val fragment: Fragment
) : ListAdapter<Int, MineOthersAdapter.MyOthersViewHolder>(DiffCallback) {

    inner class MyOthersViewHolder(
        val binding: ItemMineOthersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: Int) {
            binding.apply {
                this.name = name
                if (type == OTHER_OPTION) {
                    binding.rlOthers.setOnClickListener {
                        if (viewModel.optSwitch[layoutPosition] == true) {
                            if (layoutPosition == PERSONAL_SETTING || layoutPosition == SHIELD_SETTING || layoutPosition == UPDATE) {
                                val action =
                                    MineFragmentDirections.actionMineFragmentToItemMineFragment(
                                        layoutPosition
                                    )
                                it.findNavController().navigate(action)
                            } else if (layoutPosition == SHARE) {
                                initShareCardView()
                            } else if (layoutPosition == LOGOUT) {
                                initLogOutDialog()
                            } else {
                                val action =
                                    MineFragmentDirections.actionMineFragmentToItemDetailFragment2(
                                        layoutPosition, true
                                    )
                                it.findNavController().navigate(action)
                            }
                        } else {
                            Toast.makeText(fragment.context,"功能正在维护！",Toast.LENGTH_SHORT).show()
                        }
                    }
                } else if (type == DETAIL) {
                    if (name == R.string.check_update) {
                        binding.rlOthers.setOnClickListener {
                                //viewModel.checkVersion(fragment as ItemMineFragment)
                            viewModel.initialNotification(fragment as ItemMineFragment)
                        }
                    }else {
                        binding.rlOthers.setOnClickListener {
                            val action =
                                ItemMineFragmentDirections.actionItemMineFragmentToItemDetailFragment2(
                                    name, viewModel.isVerifiedEmail.value!!
                                )
                            fragment.findNavController().navigate(action)
                        }
                    }
                }
            executePendingBindings()
            }
        }

        @SuppressLint("InflateParams")
        private fun initShareCardView() {
            val shareCardView = LayoutInflater.from((fragment as MineFragment).context)
                .inflate(R.layout.ppw_share, null)
            val shareCard = shareCardView.findViewById<LinearLayout>(R.id.share_card)
            val cancel = shareCardView.findViewById<TextView>(R.id.share_cancel_button)
            val ppwShare = PopupWindow(shareCardView)
            ppwShare.isOutsideTouchable = true  //点击卡片外部退出
            ppwShare.isFocusable = true     //按返回键允许退出
            ppwShare.width = ViewGroup.LayoutParams.MATCH_PARENT
            ppwShare.height = ViewGroup.LayoutParams.WRAP_CONTENT
            val lp = fragment.requireActivity().window.attributes
            lp.alpha = 0.6f // 0.0~1.0   减弱背景亮度
            fragment.requireActivity().window.attributes = lp
            ppwShare.showAtLocation(
                fragment.requireActivity().window.decorView, Gravity.BOTTOM, 0,
                0
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
                    MineFragmentDirections.actionMineFragmentToItemDetailFragment2(
                        SHARE, true
                    )
                fragment.findNavController().navigate(action)
            }
        }

        private fun initLogOutDialog() {
            val dialog = Dialog(fragment.requireContext())
            val dialogView = fragment.requireActivity().layoutInflater.inflate(R.layout.dialog_logout, null)
            dialog.setContentView(dialogView)
            val btnCancel = dialogView.findViewById<Button>(R.id.cancel)
            val btnLogout = dialogView.findViewById<Button>(R.id.logout)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnLogout.setOnClickListener {
                dialog.dismiss()
                val mmkvUtil = MMKVUtil.getMMKV(fragment.context)
                mmkvUtil.put(Constant.USER_TOKEN, "")
                mmkvUtil.put(Constant.USER_TOKEN_V2, "")
                mmkvUtil.put(Constant.IS_LOGIN, false)
                ARouter.getInstance().build("/loginAndRegister/LARActivity").navigation()
                fragment.requireActivity().finish()
            }
            dialog.show()
        }

        private fun cancelDarkBackGround() {
            val lp = (fragment as MineFragment).requireActivity().window.attributes
            lp.alpha = 1f // 0.0~1.0
            fragment.requireActivity().window.attributes = lp
        }   //取消暗背景
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOthersViewHolder {
        return MyOthersViewHolder(ItemMineOthersBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MyOthersViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return true
        }
    }
}