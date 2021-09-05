package cn.pivotstudio.husthole.view.homescreen.mine.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.pivotstudio.husthole.R;

public class PageLawFragment {
    View law_view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        law_view = inflater.inflate(R.layout.activity_rules, container, false);
        init();
        return law_view;
    }
    private void init(){
        TextView law_content = (TextView) law_view.findViewById(R.id.law_content);
        law_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        String _content = ("<small><font color=\"#00000000\">你好，这里是1037树洞~</font></small><br>"+
                " <small><font color=\"#666666\">为了所有用户都能够愉快的使用这个社区， 我们需要设定一些社区规范来约束大家的行为。同时， 我们也会详细地说明大家在社区中活动的权利和义务。</font><br><br>"+
                "<small><font color=\"#00000000\">你的权利</font></small><br><br>"+
                "<small><font color=\"#666666\">-你可以享受社区的所有功能， 并参与社区秩序维护； 如果认为发言违反了社区规范， 可以提交举报。</font></small><br>"+
                "<small><font color=\"#666666\">-如果你的发言被举报删除，你有权知晓背后的具体原因， 我们也会向你说明情况。</font></small><br><br>"+
                "<small><font color=\"#00000000\">你的义务</font></small><br><br>"+
                "<small><font color=\"#666666\">-做一个友善的倾听者， 尊重他人， 即使TA 与你观点相异。</font></small><br>"+
                "<small><font color=\"#666666\">-不要发布令人感到不适或者违反法律法规的内容， 包括但不限于侮辱他人、侵犯隐秘、发布暴力或色情内容等。</font></small><br>"+
                "<small><font color=\"#666666\"></font>-在参与讨论时， 请与我们一起维护社区的安全， 对于社区内今人不适的内容主动制止。</small><br><br>"+

                "<small><font color=\"#00000000\" size=\"15sp\">社区规范</font></small><br>"+
                "<small><font color=\"#00000000\">一、管理主体和适用法律</font></small><br><br>"+
                "<small><font color=\"#666666\">1037 树洞由PivotStudio 工作室开发， 并由PivotStudio 工作室运营组主持运营，1037树洞运营团队（ 以下简称运营团队）负责具体运营工作，承担树洞管理具体职责， 依据本规范对树洞进行管理。</font></small><br>"+
                "<small><font color=\"#666666\">本社区开启华中大邮箱注册机制， 面向用户为与华中科技大学相关的个人， 例如学生、校友等。本社区根据《中华人民共和国宪法》《最高人民法院关于审理侵害信息网络传播权民事纠纷案件适用法律若干问题的规定》《侵权责任法》第36 条“ 关于网络侵权的规定\" 《中华人民共和国计算机信息网络国际联网管理规定》《中华人民共和国网络安全法》及《互联网信息服务管理办法》《互联网电子公告服务管理规定》《互联网论坛社区服务管理规定》《互联网跟帖评论服务管理规定》的规定， 并结合合普通高等学校相关管理规定， 结合华中科技大学校规制定本规范， 简称《规范》或《树洞条例》。</font></small><br><br>"+

                "<small><font color=\"#00000000\">二、版权与责任</font></small><br><br>"+
                "<small><font color=\"#666666\">用户使用社区并上传任何信息时， 即确认其享有所发布内容的版权（ 比如原创），或者使用内容属于教育、科学研究、评论与报道等” 合理使用” 的范畴。如果用户享有内容的版权， 即永久授权社区对其的发布与传播。</font></small><br>"+
                "<small><font color=\"#666666\">用户对在社区上发表的内容、言论</font></small><br>"+
                "<small><font color=\"#00000000\">由用户自身承担一切法律责任</font></small><br>"+
                "<small><font color=\"#666666\">,PivotStudio 工作室和运营团队</font></small><br>"+
                "<small><font color=\"#00000000\">只承担监管责任。</font></small><br><br>"+

                "<small><font color=\"#00000000\">三、账户管理</font></small><br><br>"+
                "<small><font color=\"#666666\">1.社区的账户仅供本人使用。账户的创立需经过（ 一） 所规定的符合条件个人的验证。不满足使用条件的用户账户将被停用。</font></small><br>"+
                "<small><font color=\"#666666\">2.社区一旦发现账户被多人使用， 即有权无条件立即停用账户。</font></small><br>"+
                "<small><font color=\"#666666\">3.社区有权以任何理由拒绝向用户提供服务。</font></small><br><br>"

//            "<small><font color=\"#00000000\"></font></small><br>"+
//            "<small><font color=\"#666666\"></font></small><br>"+
//            ""
        );
        law_content.setText(Html.fromHtml(_content));

    }




}
