package cn.pivotstudio.modulep.hole.ui.adapter

import androidx.databinding.BindingAdapter
import android.widget.TextView
import cn.pivotstudio.modulep.hole.R
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.Spanned
import ru.noties.markwon.Markwon
import cn.pivotstudio.moduleb.rebase.lib.base.app.BaseApplication
import cn.pivotstudio.moduleb.rebase.lib.util.emoji.SpanStringUtil
import androidx.databinding.ObservableField
import android.text.style.ForegroundColorSpan
import android.text.SpannableStringBuilder
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import cn.pivotstudio.moduleb.rebase.network.model.Reply
import cn.pivotstudio.moduleb.rebase.network.model.ReplyDto
import cn.pivotstudio.moduleb.rebase.lib.base.custom_view.EmojiEdittext
import cn.pivotstudio.modulep.hole.ui.fragment.InnerReplyFragmentDirections
import cn.pivotstudio.modulep.hole.ui.fragment.SpecificHoleFragmentDirections
import java.util.regex.Pattern

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
fun setHintText(view: EmojiEdittext, reply: Reply?) {

    val replyToNickName = reply?.nickname ?: "洞主"

    val ss =
        SpannableString("回复@" + replyToNickName + (if (reply?.mine == true) "(我)" else "") + ":")
    val ass = AbsoluteSizeSpan(14, true)
    ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    view.hint = ss

}

@BindingAdapter("markDownContent", "currentId", "naviType",requireAll = true)
fun setMDContent(
    view: TextView,
    content: String?,
    currentId: String?,
    naviType: String
) {
    if (content != null) {
        val text = Markwon.markdown(BaseApplication.context!!, content.trim { it <= ' ' }
            .replace("\n", "  \n"))
        val spannableString = SpanStringUtil.getEmotionMarkdownContent(
            0x0001,
            view,
            text
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        val regexHoleId = "#\\d+"
        val patternHoleId = Pattern.compile(regexHoleId)
        val matcherHoleId = patternHoleId.matcher(spannableString)
        while (matcherHoleId.find()) {
            val key = matcherHoleId.group()
            val start = matcherHoleId.start()
            //这里限制最长树洞号为8位，百万够用了吧哈哈
            val length = key.length.coerceAtMost(8)
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(view: View) {
                    val id = key.substring(1, length)
                    if (currentId == id) {
                        Toast.makeText(view.context, "你已经在这个树洞了", Toast.LENGTH_SHORT).show()
                    } else {
                        val action: NavDirections = if(naviType == "HoleToHole") {
                            SpecificHoleFragmentDirections.actionSpecificHoleFragmentSelf(id)
                        }else {
                            InnerReplyFragmentDirections.actionInnerReplyFragmentToSpecificHoleFragment(id)
                        }
                        view.findNavController().navigate(action)
                    }
                }
            }, start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        view.text = spannableString
    }
}


@BindingAdapter("turnIcon")
fun setTurnIcon(view: ImageView, is_descend: ObservableField<Boolean>?) {
    if (is_descend != null) view.setImageResource(if (is_descend.get()!!) R.mipmap.group112 else R.mipmap.group111)
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


@BindingAdapter("replyToContent", "isMine")
fun setReplyToContent(
    view: TextView,
    relied: ReplyDto?,
    is_mine: Boolean?
) {
    if (relied == null) {
        view.text = " 该评论（or回复）已删除 "
        return
    } else {
        val redSpan = ForegroundColorSpan(view.resources.getColor(R.color.HH_OnSurface, null))
        val builder = SpannableStringBuilder("${relied.nickname} : ${relied.content}")
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        builder.setSpan(
            redSpan,
            0,
            relied.nickname.length + 1,
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
            val homepressed =
                view.context.resources.getDrawable(R.drawable.ic_pivot_studio_16dp, null)
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

@BindingAdapter("innerReplies", "innerReplyPosition")
fun setInnerReplies(view: TextView, innerList: List<Reply>, position: Int = 0) {
    innerList.getOrNull(position)?.let {

        // replied 为空，说明既不回复洞主，也不回复评论
        if (it.replied == null) return

        // replyToInner != "-1" 要渲染成三级评论的样式
        if (it.replyToInner == "-1") {
            val prefix = "${it.nickname}:"
            val builder = SpannableStringBuilder("$prefix ${it.content}")
            //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            builder.setSpan(
                ForegroundColorSpan(view.resources.getColor(R.color.HH_OnSurface, null)),
                0,
                prefix.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text = builder

        } else {
            val prefix = "${it.nickname} 回复 @${it.replied!!.nickname}:"
            val builder = SpannableStringBuilder("$prefix ${it.content}")
            builder.setSpan(
                ForegroundColorSpan(view.resources.getColor(R.color.HH_OnSurface, null)),
                0,
                it.nickname.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            builder.setSpan(
                ForegroundColorSpan(view.resources.getColor(R.color.HH_OnSurface, null)),
                prefix.length - it.replied!!.nickname.length - 2,
                prefix.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            view.text = builder
        }
        return
    }

    view.visibility = View.GONE
}
