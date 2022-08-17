package cn.pivotstudio.moduleb.libbase.util.ui

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import cn.pivotstudio.moduleb.libbase.R
import cn.pivotstudio.moduleb.libbase.util.data.TimeUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * 点赞按钮
 *
 * @param view       todo
 * @param is_thumbsUp todo
 */
@BindingAdapter("thumbupIcon")
fun onClickThumbsUp(view: ImageView, is_thumbsUp: Boolean) {
    view.setImageResource(if (is_thumbsUp) R.drawable.ic_thumbs_up_active else R.drawable.ic_thumbs_up_inactive)
}

/**
 * 回复按钮
 *
 * @param view     todo
 * @param is_reply todo
 */
@BindingAdapter("replyIcon")
fun onClickReply(view: ImageView, is_reply: Boolean) {
    view.setImageResource(if (is_reply) R.drawable.ic_comment_active_20dp else R.drawable.ic_comment_inactive_20dp)
}

/**
 * 收藏按钮
 *
 * @param view      todo
 * @param is_follow todo
 */
@BindingAdapter("followIcon")
fun onClickFollow(view: ImageView, is_follow: Boolean) {
    view.setImageResource(if (is_follow) R.drawable.ic_follow_active_20dp else R.drawable.ic_follow_inactive_20dp)
}

/**
 * 举报，删除
 *
 * @param view    todo
 * @param is_mine todo
 */
@BindingAdapter("moreListIcon")
fun onClickMore(view: ImageView, is_mine: Boolean) {
    view.setImageResource(if (is_mine) R.mipmap.vector6 else R.mipmap.vector4)
}

/**
 * 时间加载
 *
 * @param view              todo
 * @param is_last_reply     todo
 * @param created_timestamp todo
 */
@BindingAdapter("timeSign", "time")
fun onTime(view: TextView, is_last_reply: Boolean, created_timestamp: String?) {
    //后端搜索给的时间慢半个小时，麻
    val time = TimeUtil.time(created_timestamp) + if (is_last_reply) "更新" else "发布"
    view.text = time
}

/**
 * 右上角小树林icon
 *
 * @param view       todo
 * @param forestName todo
 * @param role       todo
 */
@BindingAdapter("forestIcon", "role")
fun onForestIconShow(view: Button, forestName: String, role: String?) {
    if (role == null || forestName == "") { //搜索方式获得的数据没role,麻了
        view.visibility = View.INVISIBLE
        return
    }
    when (role) {
        "pivot" -> if (forestName == "") {
            view.visibility = View.VISIBLE
            view.setPadding(15, 5, 6, 6)
            view.setTextColor(view.context.resources.getColor(R.color.GrayScale_50))
            view.text = " Pivot Studio团队 "
            val homepressed = view.context.resources.getDrawable(R.drawable.ic_pivot_studio_16dp, null)
            homepressed.setBounds(0, 0, homepressed.minimumWidth, homepressed.minimumHeight)
            view.setCompoundDrawables(homepressed, null, null, null)
            view.setBackgroundResource(R.drawable.tag_gray)
            view.setOnClickListener(null)
        } else {
            view.visibility = View.VISIBLE
            view.text = "  $forestName   "
        }
        "counseling_center" -> if (forestName == "") {
            view.visibility = View.VISIBLE
            view.setPadding(15, 5, 6, 6)
            view.setTextColor(view.context.resources.getColor(R.color.red))
            view.text = " 校心理中心  "
            val homepressed =
                view.context.resources.getDrawable(R.mipmap.counselingcenter, null)
            homepressed.setBounds(0, 0, homepressed.minimumWidth, homepressed.minimumHeight)
            view.setCompoundDrawables(homepressed, null, null, null)
            view.setBackgroundResource(R.drawable.tag_red)
            view.setOnClickListener(null)
        } else {
            view.visibility = View.VISIBLE
            view.text = "  $forestName   "
        }
        else -> view.visibility = View.VISIBLE
    }
}

/**
 * 右上角圆形icon
 *
 * @param view       todo
 * @param forestName todo
 * @param role       todo
 */
@BindingAdapter("psIcon", "role")
fun onPsIconShow(view: ImageView, forestName: String, role: String?) {
    if (role == null) {
        view.visibility = View.GONE
        return
    }
    when (role) {
        "pivot" -> if (forestName == "") {
            view.visibility = View.INVISIBLE
        } else {
            view.visibility = View.VISIBLE
            view.setImageResource(R.drawable.ic_pivot_studio_16dp)
        }
        "counseling_center" -> if (forestName == "") {
            view.visibility = View.INVISIBLE
        } else {
            view.visibility = View.VISIBLE
            view.setImageResource(R.mipmap.counselingcenter)
        }
        else -> view.visibility = View.GONE
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(it)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.icon)
            .into(imgView)
    }
}

@BindingAdapter("blurImageUrl")
fun bindBlurImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(it)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transform(
                GlideBlurTransformation(
                    imgView.context
                )
            )
            .error(R.drawable.icon)
            .into(imgView)
    }
}

/**
 * 时间加载
 *
 * @param view
 * @param created_timestamp
 */
@BindingAdapter("time")
fun setTime(view: TextView, created_timestamp: String?) {
    val time = TimeUtil.replyTime(created_timestamp) + "发布"
    view.text = time
}



