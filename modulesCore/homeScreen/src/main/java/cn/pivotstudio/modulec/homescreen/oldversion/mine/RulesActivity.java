package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import cn.pivotstudio.modulec.homescreen.R;
import com.githang.statusbar.StatusBarCompat;

public class RulesActivity extends AppCompatActivity {
    ImageView img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        img = findViewById(R.id.rules_img);
        img.setOnClickListener(v -> finish());

        init();
    }

    private void init() {

        TextView law_content = (TextView) findViewById(R.id.law_content);
        law_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        String _content = ("<font color=\"#00000000\">你好，这里是1037树洞~</font><br>"
            + " <font color=\"#666666\">为了所有用户都能够愉快的使用这个社区，我们需要设定一些社区规范来约束大家的行为。同时，我们也会详细地说明大家在社区中活动的权利和义务。</font><br><br>"
            + "<font color=\"#00000000\">你的权利</font><br><br>"
            + "<font color=\"#666666\">-你可以享受社区的所有功能并参与社区秩序维护；如果认为发言违反了社区规范，可以提交举报。</font><br>"
            + "<font color=\"#666666\">-如果你的发言被举报删除，你有权知晓背后的具体原因，我们也会向你说明情况。</font><br><br>"
            + "<font color=\"#00000000\">你的义务</font><br><br>"
            + "<font color=\"#666666\">-做一个友善的倾听者，尊重他人，即使TA 与你观点相异。</font><br>"
            + "<font color=\"#666666\">-不要发布令人感到不适或者违反法律法规的内容，包括但不限于侮辱他人、侵犯隐秘、发布暴力或色情内容等。</font><br>"
            + "<font color=\"#666666\"></font>-在参与讨论时，请与我们一起维护社区的安全，对于社区内今人不适的内容主动制止。<br><br>"
            +

            "<font color=\"#00000000\" size=\"15sp\">社区规范</font><br>"
            + "<font color=\"#00000000\">一、管理主体和适用法律</font><br><br>"
            + "<font color=\"#666666\">1037树洞由PivotStudio 工作室开发，并由PivotStudio工作室运营组主持运营，1037树洞运营团队（以下简称运营团队）负责具体运营工作，承担树洞管理具体职责，依据本规范对树洞进行管理。</font><br>"
            + "<font color=\"#666666\">本社区开启华中大邮箱注册机制，面向用户为与华中科技大学相关的个人，例如学生、校友等。本社区根据《中华人民共和国宪法》《最高人民法院关于审理侵害信息网络传播权民事纠纷案件适用法律若干问题的规定》《侵权责任法》第36条“关于网络侵权的规定\"《中华人民共和国计算机信息网络国际联网管理规定》《中华人民共和国网络安全法》及《互联网信息服务管理办法》《互联网电子公告服务管理规定》《互联网论坛社区服务管理规定》《互联网跟帖评论服务管理规定》的规定，并结合合普通高等学校相关管理规定，结合华中科技大学校规制定本规范，简称《规范》或《树洞条例》。</font><br><br>"
            +

            "<font color=\"#00000000\">二、版权与责任</font><br><br>"
            + "<font color=\"#666666\">用户使用社区并上传任何信息时，即确认其享有所发布内容的版权（比如原创），或者使用内容属于教育、科学研究、评论与报道等” 合理使用” 的范畴。如果用户享有内容的版权，即永久授权社区对其的发布与传播。</font><br>"
            + "<font color=\"#666666\">用户对在社区上发表的内容、言论</font>"
            + "<font color=\"#00000000\">由用户自身承担一切法律责任</font>"
            + "<font color=\"#666666\">，PivotStudio工作室和运营团队</font>"
            + "<font color=\"#00000000\">只承担监管责任。</font><br><br>"
            +

            "<font color=\"#00000000\">三、账户管理</font><br><br>"
            + "<font color=\"#666666\">1.社区的账户仅供本人使用。账户的创立需经过（一） 所规定的符合条件个人的验证。不满足使用条件的用户账户将被停用。</font><br>"
            + "<font color=\"#666666\">2.社区一旦发现账户被多人使用，即有权无条件立即停用账户。</font><br>"
            + "<font color=\"#666666\">3.社区有权以任何理由拒绝向用户提供服务。</font><br><br>"
            +

            "<font color=\"#00000000\">四、违规内容</font><br>"
            + "<font color=\"#666666\">1.</font>"
            + "<font color=\"#00000000\">违反《中华人民共和国宪法》等（一） 中规定的法律法规的内容。</font><br>"
            + "<font color=\"#666666\">2.</font>"
            + "<font color=\"#00000000\">侮辱他人或特定人群的内容。</font>"
            + "<font color=\"#666666\">如： 煽动民族矛盾、煽动院系攻击、地域歧视、对个人或人群进行侮辱性言攻击等。</font><br>"
            + "<font color=\"#666666\">3.</font>"
            + "<font color=\"#00000000\">侵犯他人隐私的内容。</font><br>"
            + "<font color=\"#666666\">4.</font>"
            + "<font color=\"#00000000\">侵犯他人知识产权、商业机密的内容。</font><br>"
            + "<font color=\"#666666\">5.</font>"
            + "<font color=\"#00000000\">散布谣言和恐慌、可能会误导他人的内容。</font>"
            + "<font color=\"#666666\">即无权威来源、表述不含有” 可能\" ” 听说” 等类似限定词的可能具有公众影晌的内容。经过任何官方媒体、机构辟谣的内容，应当附有辟谣信息。</font><br>"
            + "<font color=\"#666666\">6.</font>"
            + "<font color=\"#00000000\">鼓励、教唆他人实施违法犯罪、暴力、非法集会的内容。</font><br>"
            + "<font color=\"#666666\">7.</font>"
            + "<font color=\"#00000000\">色情内容。</font>"
            + "<font color=\"#666666\">含有任何淫秽、色情、性暗示、不当暴露的内容。</font><br>"
            + "<font color=\"#666666\">8.</font>"
            + "<font color=\"#00000000\">政治敏感内容。</font>"
            + "<font color=\"#666666\"></font>含有隐喻、容易引起歧义的缩写、代名词、特定表述等对于政治等敏感问题进行引导、讨论。<br>"
            + "<font color=\"#666666\">9.</font>"
            + "<font color=\"#00000000\">干扰社区正常运营，大量重复内容垃圾发帖等。</font><br>"
            + "<font color=\"#666666\">10.</font>"
            + "<font color=\"#00000000\">引起他人不适的内容。</font>"
            + "<font color=\"#666666\">即经过社区用户大量举报的内容。</font><br>"
            + "<font color=\"#666666\">11.</font>"
            + "<font color=\"#00000000\">经本人要求删除涉及个人隐私、过多细节或对本人生活造成不良影响的发帖。</font><br><br>"
            +

            "<font color=\"#00000000\">五、违规处理机制</font><br>"
            + "<font color=\"#666666\">1.</font>"
            + "<font color=\"#00000000\">删帖处罚</font>"
            + "<font color=\"#666666\">：出现违规行为的帖子将会被删除。</font><br>"
            + "<font color=\"#666666\">2.</font>"
            + "<font color=\"#00000000\">禁言处罚</font>"
            + "<font color=\"#666666\">：发表违规内容的用户将视情节轻重给予1天，3天，5天，15天，30天的禁言处罚。</font><br>"
            + "<font color=\"#666666\">3.</font>"
            + "<font color=\"#00000000\">永久封禁</font>"
            + "<font color=\"#666666\">：出现违反（四） 中第5，6条的行为，第一次违反将得到一次</font>"
            + "<font color=\"#00000000\">警告</font>"
            + "<font color=\"#666666\">，第二次违反直接</font>"
            + "<font color=\"#00000000\">永久封禁。</font><br>"
            + "<font color=\"#666666\">4.用户被处罚后，有权获悉具体的处罚信息，包括删帖依据、禁言时间，具体信息将发送到用户的消息盒里。</font><br>"
            + "<font color=\"#666666\">5.管理团队有权对任何处罚进行调整。</font><br><br>"
            +

            "<font color=\"#00000000\">六、举报与申诉</font><br><br>"
            + "<font color=\"#666666\">1.用户在使用过程中如发现违反本规范中所述的行为，可通过1037树洞内的举报按钮或通过邮联系运营团队进行举报。</font><br>"
            + "<font color=\"#666666\">2.用户被封禁后在封禁期满前有权通过邮联系运营团队书面提出申诉。</font><br><br>"
            +

            "<font color=\"#00000000\">七、回帖准则</font><br><br>"
            + "<font color=\"#666666\">回帖内容要求与发帖内容要求相同，并遵循同样的处罚机制。鉴于回帖与发帖的展现形式有所差异，在此补充几条规范：</font><br>"
            + "<font color=\"#666666\">1.</font>"
            + "<font color=\"#00000000\">禁止言语攻击他人。</font>"
            + "<font color=\"#666666\">请理性地回应观点而不要攻击发表观点的人。</font><br>"
            + "<font color=\"#666666\">2.</font>"
            + "<font color=\"#00000000\">不建议“歪楼”。</font>"
            + "<font color=\"#666666\">即偏题、带节奏行为。如一些无意义的赞美，互吹和乱膜。</font><br>"
            + "<font color=\"#666666\">3.</font>"
            + "<font color=\"#00000000\">不建议发表过多重复内容。</font>"
            + "<font color=\"#666666\">即常说的跟队形，个别情况，如刷“99”等除外。</font><br>"
            + "<font color=\"#666666\">对于违反上述条例的回帖，我们可能会进行” 折叠” 处理，即评论回复不可见，情节严重者将给予禁言处理。</font><br><br>"
            +

            "<font color=\"#00000000\">八、技术性限制</font><br><br>"
            + "<font color=\"#666666\">1.管理团队为了保护社区软件平台的合理运行，有权对于行为异常的用户的账号加以技术性限制，包括但不限于限制某些功能的访问。</font><br>"
            + "<font color=\"#666666\">2.用户禁止未经允许，对社区平台进行漏洞扫描、流量攻击、压力测试等可能会对社区平台造成破环、数据损失、隐私泄露的操作。</font><br>"
            + "<font color=\"#666666\">3.不得使用微信小程序以外的客户端、脚本等访问方式，未经运营团队明确许可，访问1037树洞的相关服务、下载1037树洞服务提供的相关内容；违者造成恶劣影晌的，树洞运营团队有权调查、封禁传播人账户，并追究法律责任。</font><br><br>"
            +

            "<font color=\"#00000000\">九、个人隐私</font><br><br>"
            + "<font color=\"#666666\">1.为了社区系统的正常运营，我们可能会收集您的个人信息。这些信息由您提供或您的设备产生，并传输到社区的服务器上，包括但不限于：您的邮箱地址、操作系统版本、浏览器版本、IP地址等；我们会妥善保管您的个人信息，不向任何个人或机构有偿提供您的信息。我们不会将您的信息用于盈利。</font><br>"
            + "<font color=\"#666666\">2.为获悉违规用户身份信息，删帖封禁等用途，您所发送的匿名帖子的内容与您的账户相关联。您的个人信息在关联时是经过rsa和aes算法加密的。除了具有服务器管理员权限的管理人员以外，仅具有数据库访问权限的管理人员无法在合理的时间内对应内容和账户，除非密码学出现重大崩溃。即便如此，我们无法防止用户本身选择在匿名社区发送个人信息的行为，因此建议您妥善保管个人信息并保护个人隐私。</font><br>"
            + "<font color=\"#666666\">3.如果没有执法部门或其他政府机构的特殊保密要求，我们将开所有向1037树洞运营团队请求查询用户注册邮箱和学号明文的请求和原因。</font><br><br>"
            +

            "<font color=\"#00000000\">十、用户信息查询</font><br><br>"
            + "<font color=\"#666666\">运营团队未经用户允许，不主动尝试获取用户的身份信息，以下特定情形除外：</font><br>"
            + "<font color=\"#666666\">1.用户转让、出租、出售个人账户或者个人账户导致的个人信息泄露；</font><br>"
            + "<font color=\"#666666\">2.为维护社会公共利益、校园安全稳定以及个人人身安全；</font><br>"
            + "<font color=\"#666666\">3.根相关法律法规或政策的要求。</font><br>"
            + "<font color=\"#666666\">运营团队尝试获取用户信息需要经过运营团队全体管理员和半数以上成员同意。</font><br><br>"
            +

            "<font color=\"#00000000\">十一、法律冲突</font><br><br>"
            + "<font color=\"#666666\">本规范不构成对相关法律法规的任何有效修改，如有冲突，以相关法律法规与制度文件为准。</font><br><br>"
            +

            "<font color=\"#00000000\">十二、修订</font><br><br>"
            + "<font color=\"#666666\">本条例不定时修订。若本条例进行修订，修订信息将在社区内公示。在2021年1月1日后，公示期将至少提高至48小时。</font><br>"
            + "<font color=\"#666666\">本规范由1037树洞开发团队PivotStudio 工作室负责解释。树洞运营组联系邮箱：husthole@pivotstudio.cn</font><br><br>");
        law_content.setText(Html.fromHtml(_content));
    }
}
