package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
class MineOthersAdapter : ListAdapter<Int, MineOthersAdapter.MyOthersViewHolder>(DiffCallback) {

    private lateinit var onItemClickListener: OnItemClickListener
    inner class MyOthersViewHolder(
        val binding: ItemMineOthersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: Int) {
            binding.apply {
                this.name = name
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
        onItemClickListener.let {
            holder.binding.rlOthers.apply {
                setOnClickListener {
                    onItemClickListener.onClick(it, position, item)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int, nameID: Int)
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