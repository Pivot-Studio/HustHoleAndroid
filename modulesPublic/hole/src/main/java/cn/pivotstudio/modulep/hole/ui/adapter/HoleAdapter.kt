package cn.pivotstudio.modulep.hole.ui.adapter

import androidx.databinding.BindingAdapter
import android.widget.TextView
import cn.pivotstudio.modulep.hole.R
import android.widget.EditText
import cn.pivotstudio.modulep.hole.model.ReplyListResponse.ReplyResponse
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.Spanned
import ru.noties.markwon.Markwon
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.moduleb.libbase.util.emoji.SpanStringUtil
import androidx.databinding.ObservableField
import android.text.style.ForegroundColorSpan
import android.text.SpannableStringBuilder
import android.text.Spannable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.pivotstudio.husthole.moduleb.network.model.Reply

/**
 * @classname:HoleAdapter
 * @description:
 * @date:2022/5/9 0:49
 * @version:1.0
 * @author:
 */


@BindingAdapter("aliasName", "aliasNameOwner")
fun setAlias(v: TextView, alias: String, is_mine: Boolean) {
    v.text = if (is_mine) "$alias(我)" else alias
}


@BindingAdapter("emojiBar")
fun showEmojiBar(view: ImageView, showingEmojiPad: Boolean?) {
    if (showingEmojiPad != null) {
        view.setImageResource(if (!showingEmojiPad) R.drawable.ic_emoji_28dp else R.drawable.ic_keyboard_28dp)
    }
}


@BindingAdapter("hintText")
fun setHintText(view: EditText, answered: ReplyResponse?) {
    if (answered != null) {
        val ss =
            SpannableString("回复@" + answered.alias + (if (answered.is_mine) "(我)" else "") + ":")
        val ass = AbsoluteSizeSpan(14, true)
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.hint = ss
    }
}


@BindingAdapter("markDownContent")
fun setMDContent(view: TextView, content: String?) {
    if (content != null) {
        val text = Markwon.markdown(BaseApplication.context!!, content.trim { it <= ' ' }
            .replace("\n", "  \n"))
        val spannableString = SpanStringUtil.getEmotionMarkdownContent(
            0x0001,
            BaseApplication.context,
            view,
            text
        )
        view.text = spannableString
    }
}


@BindingAdapter("turnIcon")
fun setTurnIcon(view: ImageView, is_descend: ObservableField<Boolean>?) {
    if (is_descend != null) view.setImageResource(if (is_descend.get()!!) R.mipmap.group112 else R.mipmap.group111)
}

/**
 * 修改只看洞主图标
 *
 * @param view
 * @param is_owner
 */
@BindingAdapter("ownerText")
fun setOwnerText(view: Button, is_owner: ObservableField<Boolean>?) {
    if (is_owner != null) {
        if (is_owner.get()!!) {
            view.setPadding(20, 5, 6, 6)
            view.setTextColor(view.resources.getColor(R.color.HH_BandColor_3))
            val homepressed = view.resources.getDrawable(R.mipmap.vector8, null)
            homepressed.setBounds(0, 0, homepressed.minimumWidth, homepressed.minimumHeight)
            view.setCompoundDrawables(homepressed, null, null, null)
        } else {
            view.setPadding(0, 0, 0, 0)
            view.setCompoundDrawables(null, null, null, null)
            view.setTextColor(view.resources.getColor(R.color.GrayScale_50))
        }
    }
}

/**
 * 设置热评icon
 *
 * @param view
 * @param is_hot 是否是热评
 */
@BindingAdapter("hotIcon")
fun setHotIcon(view: TextView, is_hot: Boolean) {
    view.visibility = if (is_hot) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("replyContentShow")
fun setContentVisibility(view: ConstraintLayout, reply_to: Int) {
    view.visibility = if (reply_to == -1) View.GONE else View.VISIBLE
}


@BindingAdapter("replyContentShow")
fun setContentVisibility(view: ConstraintLayout, replyTo: String) {
    view.visibility = if (replyTo == "-1") View.GONE else View.VISIBLE
}


@BindingAdapter("replyToContent", "replyToAlias", "isMine")
fun setReplyToContent(
    view: TextView,
    content: String,
    nickname: String,
    is_mine: Boolean?
) {
    if (content == "") {
        view.text = " 该评论（or回复）已删除 "
    } else {
        val redSpan = ForegroundColorSpan(view.resources.getColor(R.color.GrayScale_0, null))
        val builder = SpannableStringBuilder("$nickname : $content")
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        builder.setSpan(
            redSpan,
            0,
            nickname.length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = builder
    }
}

/**
 * 右上角小树林icon
 *
 * @param view
 * @param forestName
 * @param role
 */
@BindingAdapter("forestIcon", "role")
fun setForestIcon(view: Button, forestName: String, role: String?) {
    if (role == null) { //后端搜索出来的数据没role,麻了
        view.visibility = View.INVISIBLE
        return
    }
    when (role) {
        "pivot" -> if (forestName == "") {
            view.visibility = View.VISIBLE
            view.setPadding(15, 5, 6, 6)
            view.setTextColor(view.context.resources.getColor(R.color.GrayScale_50))
            view.text = " Pivot Studio团队 "
            val homepressed = view.context.resources.getDrawable(R.mipmap.pivot, null)
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
        else -> view.visibility = View.INVISIBLE
    }
}

/**
 * 右上角圆形icon
 *
 * @param view
 * @param forestName
 * @param role
 */
@BindingAdapter("psIcon", "role")
fun setCircleIcon(view: ImageView, forestName: String, role: String?) {
    if (role == null) {
        view.visibility = View.INVISIBLE
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
        else -> view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("innerReplies")
fun setInnerReplies(view: TextView, innerList: List<Reply>) {
    innerList.firstOrNull()?.let {
        val redSpan = ForegroundColorSpan(view.resources.getColor(R.color.GrayScale_0, null))
        val builder = SpannableStringBuilder("${it.nickname} : ${it.content}")
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        builder.setSpan(
            redSpan,
            0,
            it.nickname.length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = builder
    }
}
