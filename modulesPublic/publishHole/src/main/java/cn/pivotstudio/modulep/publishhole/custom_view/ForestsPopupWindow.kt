package cn.pivotstudio.modulep.publishhole.custom_view

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.moduleb.libbase.base.custom_view.MaxHeightRecyclerView
import cn.pivotstudio.modulep.publishhole.R
import cn.pivotstudio.modulep.publishhole.ui.activity.PublishHoleActivity
import cn.pivotstudio.modulep.publishhole.ui.adapter.ChooseForestAdapter
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel

/**
 * @classname: ForestsPopupWindow
 * @description: 选择小树林的ppw
 * @date: 2022/5/6 14:55
 * @version: 1.0
 * @author:
 */
class ForestsPopupWindow(
    private val context: Context
) : PopupWindow() {
    lateinit var recyclerView: MaxHeightRecyclerView
    private val chooseForestAdapter: ChooseForestAdapter by lazy { ChooseForestAdapter(context, this) }
    private lateinit var backIv: ImageView
    private lateinit var chooseBtn: Button

    /**
     * 初始化view操作
     */
    private fun initView() {
        val v = LayoutInflater.from(context).inflate(R.layout.ppw_publishhole_forests, null)
        contentView = v

        backIv = v.findViewById(R.id.iv_ppwpublishhole_finish)
        chooseBtn = v.findViewById(R.id.btn_publishholeforests_chooseforest)
        recyclerView = v.findViewById(R.id.rv_ppwpublishhole)

        val display = (context as Activity).windowManager.defaultDisplay
        val outSize = Point()
        // 通过Display对象获取屏幕宽、高数据并保存到Point对象中
        display.getSize(outSize)
        // 从Point对象中获取宽、高
        val x = outSize.x
        val y = outSize.y //此为屏幕高度
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = y - 300 //写死高度，不写死，会随着item数量变化，ppw上端上下跳动
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        isOutsideTouchable = true

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chooseForestAdapter

        backIv.setOnClickListener { v: View -> onClick(v) }
        chooseBtn.setOnClickListener { v: View -> onClick(v) }
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        setOnDismissListener {
            val animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(300)
            animator.addUpdateListener { animation -> setBackgroundAlpha(animation.animatedValue as Float) }
            animator.start()
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    private fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_publishholeforests_chooseforest) {
            val publishHoleViewModel = ViewModelProvider(
                (context as PublishHoleActivity)
            )[PublishHoleViewModel::class.java]

            publishHoleViewModel.forestId = null
            publishHoleViewModel.forestName.value = "未选择任何小树林"
            dismiss()
        } else if (id == R.id.iv_ppwpublishhole_finish) {
            dismiss()
        }
    }

    /**
     * 实际改变背景透明度
     *
     * @param bgAlpha
     */
    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (context as Activity).window.attributes
        lp.alpha = bgAlpha
        context.window.attributes = lp
    }

    /**
     * 显式后面的半黑屏
     */
    fun show() {
        val animator = ValueAnimator.ofFloat(1f, 0.5f).setDuration(300)
        animator.addUpdateListener { animation -> setBackgroundAlpha(animation.animatedValue as Float) }
        animator.start()
    }

    fun setJoinedForests(forests: List<ForestBrief>) {
        chooseForestAdapter.changeDataJoinedForest(forests)
    }

    fun setAllForests(allForests: List<Pair<String, List<ForestBrief>>>) {
        chooseForestAdapter.changeDataDetailTypeForest(allForests)
    }

    init {
        initView()
        initListener()
    }
}