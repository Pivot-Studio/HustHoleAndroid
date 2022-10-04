package cn.pivotstudio.modulep.publishhole.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.modulep.publishhole.BuildConfig
import cn.pivotstudio.modulep.publishhole.R
import cn.pivotstudio.modulep.publishhole.custom_view.ForestsPopupWindow
import cn.pivotstudio.modulep.publishhole.databinding.ItemPublishholeForestlistBinding
import cn.pivotstudio.modulep.publishhole.databinding.ItemPublishholeForesttypeBinding
import cn.pivotstudio.modulep.publishhole.ui.activity.PublishHoleActivity
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel

/**
 * @classname: ForestListElAdapter
 * @description: Elv的适配器
 * @date: 2022/5/6 22:42
 * @version: 1.0
 * @author:
 */
class ForestListElAdapter(
    /**
     * 构造函数
     *
     * @param context
     * @param mForestType 小树林类型
     * @param mPpw        外层ppw
     */
    private val context: Context,
    private val mForestType: List<String>, //用来在点击后将ppw关闭，起始通过dataBinding方式设置点击事件，也得通过setPPw方式绑定方法位置，所以这里没有在xml中进行点击事件绑定
    private val forestsPopupWindow: ForestsPopupWindow
) : BaseExpandableListAdapter() {

    private val publishHoleViewModel = ViewModelProvider(
        (context as PublishHoleActivity)
    )[PublishHoleViewModel::class.java]

    private var mForestLists: List<Pair<String, List<ForestBrief>>> = mutableListOf()

    /**
     * 更新数据
     *
     * @param mForests
     */
    fun changeDataForests(forests: List<Pair<String, List<ForestBrief>>>) {
        mForestLists = forests
    }

    override fun getGroupCount(): Int {
        return mForestLists.size
    }

    override fun getChildrenCount(i: Int): Int {
        return mForestLists[i].second.size
    }

    override fun getGroup(i: Int): Any {
        return mForestLists[i].first
    }

    override fun getChild(i: Int, i1: Int): Any {
        return mForestLists[i].second[i1]
    }

    override fun getGroupId(i: Int): Long {
        return i.toLong()
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return i1.toLong()
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    override fun hasStableIds(): Boolean {
        return true
    }

    /**
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded    该组是展开状态还是伸缩状态，true=展开
     * @param convertView   重用已有的视图对象
     * @param parent        返回的视图对象始终依附于的视图组
     */
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {

        var view = convertView
        val groupViewHolder: GroupTitleViewHolder
        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_publishhole_foresttype, parent, false)
            groupViewHolder = GroupTitleViewHolder()
            groupViewHolder.parentImage = view.findViewById(R.id.iv_publishholetype_choosetype)
            groupViewHolder.titleTv = view.findViewById(R.id.tv_publishholeforesttype_name)
            groupViewHolder.image = view.findViewById(R.id.iv_publishholeforestype_icon)
            view.tag = groupViewHolder
        } else {
            groupViewHolder = view.tag as GroupTitleViewHolder
        }

        groupViewHolder.titleTv!!.text = mForestType[groupPosition]
        ForestItemAdapter.getUrlFormLocal(groupViewHolder.image, mForestType[groupPosition])
        if (isExpanded) {
            groupViewHolder.parentImage!!.setImageResource(R.mipmap.triangle_5)
        } else {
            groupViewHolder.parentImage!!.setImageResource(R.mipmap.triangle_3)
        }
        return view!!
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val childViewHolder: ChildViewHolder
        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_publishhole_forestlist, parent, false)
            childViewHolder = ChildViewHolder()
            //数据绑定
            view.setTag(R.id.tag_first, childViewHolder) //id必须保证唯一，所以需要自定义
            view.setTag("layout/item_publishhole_forestlist_0") //自定义view必须的加，具体字符串在build文件里面找
            childViewHolder.binding = DataBindingUtil.bind(view) //自定义view必须的使用bind方式
        } else {
            childViewHolder = view.getTag(R.id.tag_first) as ChildViewHolder
        }
        childViewHolder.binding!!.forest = mForestLists[groupPosition].second[childPosition]
        childViewHolder.binding!!.btnChooseForest.setOnClickListener {
            publishHoleViewModel.apply {
                forestId = mForestLists[groupPosition].second[childPosition].forestId
                forestName.value = mForestLists[groupPosition].second[childPosition].forestName
            }
            forestsPopupWindow.dismiss()
        }
        childViewHolder.binding!!.ivPublishholeforestlistIcon.setOnClickListener { v: View? ->
            if (!BuildConfig.isRelease) {
                Toast.makeText(context, "当前为模块测试阶段", Toast.LENGTH_SHORT).show()
            }
        }
        return view!!
    }

    //指定位置上的子元素是否可选中
    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return true
    }

    internal class GroupTitleViewHolder {
        var binding: ItemPublishholeForesttypeBinding? = null
        var titleTv: TextView? = null
        var parentImage: ImageView? = null
        var image: ImageView? = null
    }

    internal class ChildViewHolder {
        var binding: ItemPublishholeForestlistBinding? = null
    }
}