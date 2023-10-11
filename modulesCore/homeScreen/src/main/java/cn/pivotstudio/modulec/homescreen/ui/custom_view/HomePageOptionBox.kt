package cn.pivotstudio.modulec.homescreen.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import cn.pivotstudio.modulec.homescreen.R

/**
 * @classname: HomePageOptionBox
 * @description: 选项栏
 * @date: 2022/5/5 2:19
 * @version:1.0
 * @author:
 */
class HomePageOptionBox : LinearLayout {
    private var mOptionsListener: OptionsListener? = null
    private var mTriangleIv: ImageView? = null
    private var mSlideBoxPpw: PopupWindow? = null
    private var mDarkScreenPpw: PopupWindow? = null
    private var mNewPublishBtn: Button? = null
    private var mNewCommentBtn: Button? = null
    private var parent: ViewGroup? = null
    var mFlag = false


    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, parent: ViewGroup) : this(context) {
        this.parent = parent
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    /**
     * 额外监听器
     *
     * @param mOptionsListener
     */
    fun setOptionsListener(mOptionsListener: OptionsListener?) {
        this.mOptionsListener = mOptionsListener
    }

    /**
     * 初始化view
     *
     * @param context
     */
    private fun initView(context: Context) {
        if (parent != null) {
            LayoutInflater.from(context)
                .inflate(R.layout.tab_hole_list, parent, false)
        } else {
            LayoutInflater.from(context)
                .inflate(R.layout.tab_hole_list, this, true)
        }
        mTriangleIv = findViewById<View>(R.id.iv_homepage_triangle) as ImageView

        val mForestSquareCl = findViewById<LinearLayout>(R.id.ll_mid_block)
        mForestSquareCl.setOnClickListener { v: View -> onClick(v) }

        val contentView = LayoutInflater.from(context).inflate(R.layout.ppw_homepage, null, false)
        val darkScreen = LayoutInflater.from(context).inflate(R.layout.ppw_homepagedarkscreen, null)
        mSlideBoxPpw = PopupWindow(contentView)

        mDarkScreenPpw = PopupWindow(darkScreen)
        mNewPublishBtn =
            contentView.findViewById<View>(R.id.btn_ppwhomepage_latest_reply) as Button
        mNewCommentBtn =
            contentView.findViewById<View>(R.id.btn_ppwhomepage_latest_publish) as Button
        mNewPublishBtn?.setOnClickListener { v: View -> onClick(v) }
        mNewCommentBtn?.setOnClickListener { v: View -> onClick(v) }
        darkScreen.setOnClickListener { v: View -> onClick(v) }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    fun onClick(v: View) {
        when (v.id) {
            R.id.ll_mid_block -> {
                if (!mFlag) {
                    beginTriangleAnim()
                } else {
                    endTriangleAnim()
                }
                mFlag = !mFlag
            }
            R.id.btn_ppwhomepage_latest_reply -> {
                mNewPublishBtn?.setBackgroundResource(R.drawable.standard_button)
                mNewPublishBtn?.setTextAppearance(v.context, R.style.popupwindow_button_click)
                mNewCommentBtn?.setBackgroundResource(R.drawable.standard_button_g95)
                mNewCommentBtn?.setTextAppearance(v.context, R.style.popupwindow_button)
                endTriangleAnim()
                mFlag = !mFlag
                mOptionsListener?.onClick(v)
            }
            R.id.btn_ppwhomepage_latest_publish -> {
                mNewCommentBtn?.setBackgroundResource(R.drawable.standard_button)
                mNewCommentBtn?.setTextAppearance(v.context, R.style.popupwindow_button_click)
                mNewPublishBtn?.setBackgroundResource(R.drawable.standard_button_g95)
                mNewPublishBtn?.setTextAppearance(v.context, R.style.popupwindow_button)
                endTriangleAnim()
                mFlag = !mFlag
                mOptionsListener?.onClick(v)
            }
            R.id.ppw_homepagedarkscreen -> {
                endTriangleAnim()
                mFlag = !mFlag
                mSlideBoxPpw?.dismiss()
                mDarkScreenPpw?.dismiss()
            }
        }
    }

    /**
     * 启动树洞广场后的动画
     */
    private fun beginTriangleAnim() {
        val rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 200
        rotate.fillAfter = true
        mTriangleIv?.startAnimation(rotate)

        mDarkScreenPpw?.let {
            with(it) {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = R.style.darkScreenAnim
                showAsDropDown(this@HomePageOptionBox)
            }
        }
        mSlideBoxPpw?.let {
            with(it) {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = R.style.contextMenuAnim
                showAsDropDown(this@HomePageOptionBox)
            }
        }
    }

    /**
     * 关闭树洞广场选项的动画
     */
    fun endTriangleAnim() {
        val rotate = RotateAnimation(
            180f, 360f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 200
        rotate.fillAfter = true
        mTriangleIv?.startAnimation(rotate)
        mSlideBoxPpw?.dismiss()
        mDarkScreenPpw?.dismiss()
    }
}