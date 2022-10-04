package cn.pivotstudio.modulep.publishhole.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.moduleb.libbase.base.ui.adapter.BaseRecyclerViewAdapter
import cn.pivotstudio.modulep.publishhole.R
import cn.pivotstudio.modulep.publishhole.custom_view.ForestExpandableListView
import cn.pivotstudio.modulep.publishhole.custom_view.ForestsPopupWindow
import cn.pivotstudio.modulep.publishhole.databinding.ItemPublishholeForestlistBinding
import cn.pivotstudio.modulep.publishhole.ui.activity.PublishHoleActivity
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel

/**
 * @classname: ForestAdapter
 * @description: recyclerview的适配器
 * @date: 2022/5/6 0:54
 * @version:1.0
 * @author:
 */
class ChooseForestAdapter(
    context: Context?,
    private val forestsPopupWindow: ForestsPopupWindow
) : BaseRecyclerViewAdapter<ForestBrief, RecyclerView.ViewHolder>(context) {

    companion object {
        private const val TITLE = 0
        private const val JOINED_FORESTS = 1
        private const val FOREST = 2
        private const val BOTTOM = 3
    }

    private var mJoined: List<ForestBrief> = mutableListOf()
    private var forestWithType: List<Pair<String, List<ForestBrief>>> = mutableListOf()
    private var mElAdapter: ForestListElAdapter? = null

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == mJoined.size + 1) {
            TITLE
        } else if (position > 0 && position < mJoined.size + 1) {
            JOINED_FORESTS
        } else if (position == itemCount - 1 || position == itemCount - 2) {
            BOTTOM
        } else {
            FOREST
        }
    }

    inner class BottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTitleTv: TextView
        fun bind(position: Int) {
            mTitleTv.text = if (position == 0) "加入的小树林" else "全部小树林"
        }

        init {
            mTitleTv = itemView.findViewById(R.id.tv_publishholeforestlisttitle_title)
        }
    }

    inner class JoinedForestViewHolder(var binding: ItemPublishholeForestlistBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        var currentPosition = 0
        fun bind(position: Int) {
            this.currentPosition = position
        }

        init {
            binding.btnChooseForest.setOnClickListener {
                val publishHoleViewModel = ViewModelProvider(
                    (mContext as PublishHoleActivity)
                )[PublishHoleViewModel::class.java]

                publishHoleViewModel.forestId = mJoined[currentPosition].forestId
                publishHoleViewModel.forestName.value = mJoined[currentPosition].forestName
                forestsPopupWindow.dismiss()
            }
        }
    }

    inner class ForestTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mElv: ForestExpandableListView
        fun bind(position: Int) {}

        init {
            mElv = itemView.findViewById(R.id.elv_ppwpublishhole)
            if (mElAdapter == null) {
                mElAdapter = ForestListElAdapter(
                    mContext,
                    forestWithType.map { it.first },
                    forestsPopupWindow
                )
            }
            mElv.setAdapter(mElAdapter)
            mElAdapter?.changeDataForests(forestWithType)
            //默认展开第一个数组
            //mElv.setGroupIndicator(null);
            //mElv.expandGroup(0);
            //关闭数组某个数组，可以通过该属性来实现全部展开和只展开一个列表功能
            //expand_list_id.collapseGroup(0);
            mElv.setOnGroupClickListener { expandableListView, view, groupPosition, l ->
                if (forestWithType.isEmpty() || forestWithType[groupPosition].second.isEmpty()) {
                    showMsg("加载中...")
                    true
                } else if (forestWithType[groupPosition].second.isEmpty()) {
                    showMsg("无此类型小树林")
                    true
                } else {
                    false
                }
            }
            //子视图的点击事件
            mElv.setOnChildClickListener { expandableListView, view, groupPosition, childPosition, l -> false }
            //用于当组项折叠时的通知。
            mElv.setOnGroupCollapseListener { }
            //
            //用于当组项折叠时的通知。
            mElv.setOnGroupExpandListener { }
        }
    }

    override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TITLE -> {
                return TitleViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_publishhole_forestlisttitle, parent, false)
                )
            }
            JOINED_FORESTS -> {
                val itemPublishholeForestlistBinding =
                    DataBindingUtil.inflate<ItemPublishholeForestlistBinding>(
                        LayoutInflater.from(mContext),
                        R.layout.item_publishhole_forestlist,
                        parent,
                        false
                    )
                return JoinedForestViewHolder(itemPublishholeForestlistBinding)
            }
            FOREST -> {
                return ForestTypeViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_expandablelistview, parent, false)
                )
            }
            else -> {
                return BottomViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bottomblock, parent, false)
                )
            }
        }
    }

    override fun onBindBaseViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is JoinedForestViewHolder -> {
                val binding = holder.binding
                binding.forest = mJoined[position - 1]
                binding.executePendingBindings()
                holder.bind(position - 1)
            }
            is ForestTypeViewHolder -> {
                holder.bind(position)
            }
            is TitleViewHolder -> {
                holder.bind(position)
            }
        }
    }

    /**
     * 两个标题+两个底部支撑bottom防止遮盖+item+elv
     *
     * @return item数量
     */
    override fun getItemCount(): Int {
        return 4 + mJoined.size + if (forestWithType.isEmpty()) 0 else 1
    }

    /**
     * 添加加入的小树林
     *
     * @param mJoined
     */
    fun changeDataJoinedForest(forests: List<ForestBrief>) {
        this.mJoined = forests
        notifyDataSetChanged()
    }


    /**
     * 改变具体类型的小树林
     *
     * @param lists
     */
    fun changeDataDetailTypeForest(lists: List<Pair<String, List<ForestBrief>>>) {
        forestWithType = lists
        notifyDataSetChanged()
    }

}