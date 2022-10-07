//package cn.pivotstudio.modulec.homescreen.ui.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import androidx.recyclerview.widget.RecyclerView
//import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
//import cn.pivotstudio.moduleb.libbase.base.ui.adapter.BaseRecyclerViewAdapter
//import cn.pivotstudio.modulec.homescreen.R
//import cn.pivotstudio.modulec.homescreen.databinding.ItemHomepageholeBinding
//import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse
//import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse.DataBean
//import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
//import cn.pivotstudio.modulec.homescreen.ui.adapter.HoleRecyclerViewAdapter.HoleViewHolder.Companion.ITEM_TYPE_CONTENT
//import cn.pivotstudio.modulec.homescreen.ui.adapter.HoleRecyclerViewAdapter.HoleViewHolder.Companion.ITEM_TYPE_NO_SEARCH
//import cn.pivotstudio.modulec.homescreen.ui.adapter.HoleRecyclerViewAdapter.HoleViewHolder.Companion.ITEM_TYPE_REQUEST_FAILED
//import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
//
///**
// * @classname:HomePageHoleAdapter
// * @description:
// * @date:2022/5/4 0:26
// * @version:1.0
// * @author:
// */
//class HoleRecyclerViewAdapter(
//    private val mViewModel: HomePageViewModel,
//    context: Context
//) : BaseRecyclerViewAdapter<DataBean, RecyclerView.ViewHolder>(context) {
//    private var mHomePageHolesList: List<DataBean>? = null
//    var lastMoreListCl: ConstraintLayout? = null //保证整个recyclerView每次只有一个举报选项显式
//
//    override fun getItemViewType(position: Int): Int {
//        return if (mHomePageHolesList == null) {
//            ITEM_TYPE_REQUEST_FAILED
//        } else if (mHomePageHolesList.isNullOrEmpty()) {
//            ITEM_TYPE_NO_SEARCH
//        } else {
//            ITEM_TYPE_CONTENT
//        }
//    }
//
//    /**
//     * 如果item数量有限，item会被底部选项栏遮住，这个viewHolder补充底部块的，暂时弃用bottom
//     */
//    inner class RequestFailedHolder(view: View) : RecyclerView.ViewHolder(view)
//
//    inner class NOSearchHolder(view: View) : RecyclerView.ViewHolder(view)
//
//    /**
//     * 树洞item的viewHolder
//     */
//    inner class HoleViewHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
//        var binding: ItemHomepageholeBinding
//
//        init {
//            this.binding = binding as ItemHomepageholeBinding
////            binding.viewModel = mViewModel
////            binding.ivItemhomepageMore.setOnClickListener {
////                binding.clItemhomepageMorelist.visibility = View.VISIBLE
////                if (lastMoreListCl != null && lastMoreListCl !== binding.clItemhomepageMorelist) {
////                    lastMoreListCl!!.visibility = View.GONE
////                }
////                lastMoreListCl = binding.clItemhomepageMorelist
////            }
////            binding.clItemhomepageMorelist.setOnClickListener { v: View? ->
////                binding.clItemhomepageMorelist.visibility = View.INVISIBLE
////            }
////        }
////
//        fun bind(position: Int) {
//            binding.clItemhomepageMorelist.visibility = View.GONE
//            lastMoreListCl = null
//        }
//    }
//
//    /**
//     * 构造函数
//     *
//     * @param mViewModel 碎片的viewModel
//     * @param context
//     */
//    init {
//        mViewModel.pHomePageHoles.observe((context as HomeScreenActivity?)!!) { homepageHoleResponse: HomepageHoleResponse ->
//            val lastLength = itemCount
//            mHomePageHolesList = homepageHoleResponse.data
//            when (homepageHoleResponse.model) {
//                "REFRESH" -> notifyDataSetChanged()
//                "SEARCH_REFRESH" -> notifyDataSetChanged()
//                "LOAD_MORE", "SEARCH_LOAD_MORE" -> notifyItemRangeChanged(
//                    lastLength,
//                    homepageHoleResponse.data.size
//                )
//                "SEARCH_HOLE" -> notifyDataSetChanged()
//                "BASE" -> {}
//            }
//        }
//    }
//
//    override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            ITEM_TYPE_CONTENT -> {
//                val itemHomepageholeBinding = DataBindingUtil.inflate<ItemHomepageholeBinding>(
//                    LayoutInflater.from(mContext),
//                    R.layout.item_homepagehole,
//                    parent,
//                    false
//                )
//                HoleViewHolder(itemHomepageholeBinding)
//            }
//            ITEM_TYPE_REQUEST_FAILED -> {
//                RequestFailedHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.item_requestfailed, parent, false)
//                )
//            }
//            ITEM_TYPE_NO_SEARCH -> {
//                return NOSearchHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.item_nosearch, parent, false)
//                )
//            }
//            else -> {
//                RequestFailedHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.item_requestfailed, parent, false)
//                )
//            }
//        }
//    }
//
//    override fun onBindBaseViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is HoleViewHolder) {
//            val binding = holder.binding
//            binding.executePendingBindings()
//            holder.bind(position)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return if (mHomePageHolesList.isNullOrEmpty()) 1 else mHomePageHolesList!!.size
//    }
//
//    companion object {
//        const val ITEM_TYPE_REQUEST_FAILED = 0
//        const val ITEM_TYPE_CONTENT = 1
//        const val ITEM_TYPE_NO_SEARCH = 2
//    }
//}