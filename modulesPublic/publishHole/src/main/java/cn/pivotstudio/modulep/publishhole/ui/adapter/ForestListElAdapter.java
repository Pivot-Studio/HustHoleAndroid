package cn.pivotstudio.modulep.publishhole.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.modulep.publishhole.BuildConfig;
import cn.pivotstudio.modulep.publishhole.R;
import cn.pivotstudio.modulep.publishhole.custom_view.ForestExpandableListView;
import cn.pivotstudio.modulep.publishhole.custom_view.ForestsPopupWindow;
import cn.pivotstudio.modulep.publishhole.databinding.ItemPublishholeForestlistBinding;
import cn.pivotstudio.modulep.publishhole.databinding.ItemPublishholeForesttypeBinding;
import cn.pivotstudio.modulep.publishhole.model.DetailTypeForestResponse;
import cn.pivotstudio.modulep.publishhole.model.TypeResponse;
import cn.pivotstudio.modulep.publishhole.ui.activity.PublishHoleActivity;
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel;

/**
 * @classname:ForestListElAdapter
 * @description:Elv的适配器
 * @date:2022/5/6 22:42
 * @version:1.0
 * @author:
 */
public class ForestListElAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> mForestType;
    private List<DetailTypeForestResponse> mForestLists;
    private ForestsPopupWindow mPpw;//用来在点击后将ppw关闭，起始通过dataBinding方式设置点击事件，也得通过setPPw方式绑定方法位置，所以这里没有在xml中进行点击事件绑定

    /**
     * 构造函数
     * @param context
     * @param mForestType 小树林类型
     * @param mPpw 外层ppw
     */
    public ForestListElAdapter(Context context,List<String> mForestType,ForestsPopupWindow mPpw){
        this.context=context;
        this.mForestType=mForestType;
        this.mPpw=mPpw;
    }

    /**
     * 更新数据
     * @param mForests
     */
    public void changeDataForests(List<DetailTypeForestResponse> mForests){
        mForestLists=mForests;
    }
    @Override
    public int getGroupCount() {
        return mForestType.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mForestLists.get(i).getForests().size();
    }

    @Override
    public Object getGroup(int i) {
        return mForestType.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mForestLists.get(i).getForests().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    public boolean hasStableIds() {
        return true;
    }


/**
 *
 * 获取显示指定组的视图对象
 *
 * @param groupPosition 组位置
 * @param isExpanded 该组是展开状态还是伸缩状态，true=展开
 * @param convertView 重用已有的视图对象
 * @param parent 返回的视图对象始终依附于的视图组
 */
@Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupTitleViewHolder groupViewHolder;
        if (convertView == null){

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publishhole_foresttype,parent,false);
            groupViewHolder = new GroupTitleViewHolder();

            //未知原因绑定数据绑不上，用的原生方式
            groupViewHolder.parent_image = convertView.findViewById(R.id.iv_publishholetype_choosetype);
            groupViewHolder.titleTv=convertView.findViewById(R.id.tv_publishholeforesttype_name);
            groupViewHolder.image=convertView.findViewById(R.id.iv_publishholeforestype_icon);


            convertView.setTag(groupViewHolder);

        }else {
            groupViewHolder = (GroupTitleViewHolder)convertView.getTag();
        }
        groupViewHolder.titleTv.setText(mForestType.get(groupPosition));
        ForestItemAdapter.getUrlFormLocal(groupViewHolder.image,mForestType.get(groupPosition));

        if (isExpanded){
            groupViewHolder.parent_image.setImageResource(R.mipmap.triangle_5);
        }else{
            groupViewHolder.parent_image.setImageResource(R.mipmap.triangle_3);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publishhole_forestlist,parent,false);
            childViewHolder = new ChildViewHolder();
           //数据绑定
            convertView.setTag(R.id.tag_first,childViewHolder);//id必须保证唯一，所以需要自定义
            convertView.setTag("layout/item_publishhole_forestlist_0");//自定义view必须的加，具体字符串在build文件里面找
            childViewHolder.binding=DataBindingUtil.bind(convertView);//自定义view必须的使用bind方式

        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.id.tag_first);
        }
        childViewHolder.binding.setForest(mForestLists.get(groupPosition).getForests().get(childPosition));
        childViewHolder.binding.btnPublishholeforestlistChooseforest.setOnClickListener(v -> {
            PublishHoleViewModel publishHoleViewModel=new ViewModelProvider((PublishHoleActivity)context,new ViewModelProvider.NewInstanceFactory()).get(PublishHoleViewModel.class);
            publishHoleViewModel.setForestId(mForestLists.get(groupPosition).getForests().get(childPosition).getForest_id());
            publishHoleViewModel.pForestName.setValue(mForestLists.get(groupPosition).getForests().get(childPosition).getName());
            mPpw.dismiss();
        });
        childViewHolder.binding.ivPublishholeforestlistIcon.setOnClickListener(v -> {
            if (!BuildConfig.isRelease) {
                Toast.makeText(context, "当前为模块测试阶段", Toast.LENGTH_SHORT).show();
            } else {

            }
        });
        return convertView;
    }
    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int i, int i1) {

        return true;
    }

    static class GroupTitleViewHolder{
        ItemPublishholeForesttypeBinding binding;
        TextView titleTv;
        ImageView parent_image,image;
    }
    static class ChildViewHolder {
        ItemPublishholeForestlistBinding binding;
    }
}

