package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineOthersBinding
import cn.pivotstudio.modulec.homescreen.ui.fragment.MineFragmentDirections
import cn.pivotstudio.modulec.homescreen.ui.fragment.mine.ItemMineFragmentDirections
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.DETAIL
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.OTHER_OPTION
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.PERSONAL_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHIELD_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.UPDATE

/**
 *@classname MineSettingAdapter
 * @description:
 * @date :2022/9/23 22:35
 * @version :1.0
 * @author
 */
class MineOthersAdapter(
    private val type: Int,
    private val viewModel: MineFragmentViewModel
) : ListAdapter<Int, MineOthersAdapter.MyOthersViewHolder>(DiffCallback) {

    inner class MyOthersViewHolder(
        val binding: ItemMineOthersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: Int) {
            binding.apply {
                this.name = name
                if (type == OTHER_OPTION) {
                    binding.rlOthers.setOnClickListener {
                        if(layoutPosition == PERSONAL_SETTING || layoutPosition == SHIELD_SETTING || layoutPosition == UPDATE) {
                            val action = MineFragmentDirections.actionMineFragmentToItemMineFragment(
                                layoutPosition
                            )
                            it.findNavController().navigate(action)
                        } else {
                            val action = MineFragmentDirections.actionMineFragmentToItemDetailFragment2(
                                layoutPosition, true
                            )
                            it.findNavController().navigate(action)
                        }
                    }
                } else if (type == DETAIL) {
                    if(name == R.string.campus_email) {
                        viewModel.checkEmailVerifyState(binding)
                    }
                    binding.rlOthers.setOnClickListener {
                        val action = ItemMineFragmentDirections.actionItemMineFragmentToItemDetailFragment2(
                            name, viewModel.isVerifiedEmail.value!!
                        )
                        it.findNavController().navigate(action)
                    }
                }
                executePendingBindings()
            }
        }
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