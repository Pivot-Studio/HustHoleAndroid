package cn.pivotstudio.modulep.publishhole.custom_view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import cn.pivotstudio.moduleb.libbase.base.custom_view.MaxHeightRecyclerView;

import cn.pivotstudio.modulep.publishhole.R;
import cn.pivotstudio.modulep.publishhole.ui.activity.PublishHoleActivity;
import cn.pivotstudio.modulep.publishhole.ui.adapter.ForestRecyclerViewAdapter;
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel;

/**
 * @classname:ForestsPopupWindow
 * @description:选择小树林的ppw
 * @date:2022/5/6 14:55
 * @version:1.0
 * @author:
 */
public class ForestsPopupWindow extends PopupWindow {
    private Context context;
    public MaxHeightRecyclerView recyclerView;

    public ForestsPopupWindow(Context context) {
        this.context = context;
        initView();
        initListener();
    }

    /**
     * 初始化view操作
     */
    private void initView() {
        View v = LayoutInflater.from(context).inflate(R.layout.ppw_publishhole_forests, null);
        setContentView(v);

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        // 通过Display对象获取屏幕宽、高数据并保存到Point对象中
        display.getSize(outSize);
        // 从Point对象中获取宽、高
        int x = outSize.x;
        int y = outSize.y;//此为屏幕高度

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(y - 300);//写死高度，不写死，会随着item数量变化，ppw上端上下跳动
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);


        MaxHeightRecyclerView recyclerView = v.findViewById(R.id.rv_ppwpublishhole);
        this.recyclerView = recyclerView;


        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ForestRecyclerViewAdapter(context, this));

        ImageView backIv = v.findViewById(R.id.iv_ppwpublishhole_finish);
        Button chooseBtn = v.findViewById(R.id.btn_publishholeforests_chooseforest);
        backIv.setOnClickListener(this::onClick);
        chooseBtn.setOnClickListener(this::onClick);

    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setBackgroundAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.start();
            }
        });
    }

    /**
     * 点击事件
     *
     * @param v
     */
    private void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_publishholeforests_chooseforest) {
            PublishHoleViewModel publishHoleViewModel = new ViewModelProvider(
                    (PublishHoleActivity) context,
                    new ViewModelProvider.NewInstanceFactory()
            )
                    .get(PublishHoleViewModel.class);
            publishHoleViewModel.setForestId(0);
            publishHoleViewModel.pForestName.setValue("未选择任何小树林");
            dismiss();
        } else if (id == R.id.iv_ppwpublishhole_finish) {
            dismiss();
        }
    }

    /**
     * 实际改变背景透明度
     *
     * @param bgAlpha
     */
    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }

    /**
     * 显式后面的半黑屏
     */
    public void show() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.5f).setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setBackgroundAlpha((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}
