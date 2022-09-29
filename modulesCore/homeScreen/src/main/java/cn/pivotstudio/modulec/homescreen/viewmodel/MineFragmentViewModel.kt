package cn.pivotstudio.modulec.homescreen.viewmodel

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ActivitySecurityBinding
import cn.pivotstudio.modulec.homescreen.databinding.FragmentEvaluateBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemLabelBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineOthersBinding
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.AdviceFragment
import cn.pivotstudio.modulec.homescreen.oldversion.network.ErrorMsg
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager.API
import cn.pivotstudio.modulec.homescreen.ui.fragment.MyHoleFollowReplyFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 *@classname MineFragmentViewModel
 * @description:
 * @date :2022/9/23 21:59
 * @version :1.0
 * @author
 */
enum class Status { LOADING, DONE, ERROR }

class MineFragmentViewModel : ViewModel() {
    private val _joinDay = MutableLiveData<Int>()  //加入天数
    private val _myHoleNum = MutableLiveData<Int>() //我的树洞数
    private val _myFollowNum = MutableLiveData<Int>()   //我的关注数
    private val _myReplyNum = MutableLiveData<Int>()    //我的评论数
    private val _myNameList = MutableLiveData<List<Int>>()  //设置栏标题名称
    private val _myFragmentList = MutableLiveData<List<Fragment>>()     //viewPager存放fragment
    private val _myTabTitle = MutableLiveData<List<Int>>()  //TabLayout标题的ResId
    private val _mySettingList = MutableLiveData<List<Int>>() //个人设置标题
    private val _shieldList = MutableLiveData<List<Int>>()  //屏蔽设置标题
    private val _updateList = MutableLiveData<List<Int>>()  //更新标题
    private val _isVerifiedEmail = MutableLiveData<Boolean>()   //是否验证邮箱
    private val _isPrivacy = MutableLiveData<Boolean>() //是否选择隐私安全
    private val _communityNorm = MutableLiveData<String>()
    private val _shieldWordList = MutableLiveData<MutableList<String>>()
    private val _evalAndAdvNameList = MutableLiveData<List<Int>>()
    private val _evalAndAdvFragmentList = MutableLiveData<List<Fragment>>()

    val joinDay: LiveData<Int> = _joinDay
    val myHoleNum: LiveData<Int> = _myHoleNum
    val myFollowNum: LiveData<Int> = _myFollowNum
    val myReplyNum: LiveData<Int> = _myReplyNum
    val myNameList: LiveData<List<Int>> = _myNameList
    val myFragmentList: LiveData<List<Fragment>> = _myFragmentList
    val myTabTitle: LiveData<List<Int>> = _myTabTitle
    val mySettingList: LiveData<List<Int>> = _mySettingList
    val shieldList: LiveData<List<Int>> = _shieldList
    val updateList: LiveData<List<Int>> = _updateList
    val isVerifiedEmail: LiveData<Boolean> = _isVerifiedEmail
    val isPrivacy: LiveData<Boolean> = _isPrivacy
    val communityNorm: LiveData<String> = _communityNorm
    val shieldWordList: LiveData<MutableList<String>> = _shieldWordList
    val evalAndAdvNameList: LiveData<List<Int>> = _evalAndAdvNameList
    val evalAndAdvFragmentList: LiveData<List<Fragment>> = _evalAndAdvFragmentList

    var retrofit: Retrofit? = null
    var request: RequestInterface? = null

    init {

        RetrofitManager.RetrofitBuilder(RetrofitManager.API)
        retrofit = RetrofitManager.getRetrofit()
        request = retrofit!!.create(RequestInterface::class.java)
        _isVerifiedEmail.value = false

        initNameList()
        getMineData()
        initViewPager()
        initOption()
    }

    fun checkEmailVerifyState(
        binding: ItemMineOthersBinding
    ) {
        val call = request!!.isUnderSecurity
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    binding.tvVerify.isClickable = false
                    if (response.code() == 200) {
                        var json = "null"
                        try {
                            if (response.body() != null) {
                                json = response.body()!!.string()
                            }
                            val jsonObject = JSONObject(json)
                            _isVerifiedEmail.value =
                                jsonObject.getString("is_email_activated").toBoolean()
//                            val emailId = jsonObject.getString("email")
//                               Log.d("email",_isVerifiedEmail.value)
                            if (_isVerifiedEmail.value == true)
                                binding.tvVerify.text = "已验证"
                            else if (_isVerifiedEmail.value == false)
                                binding.tvVerify.text = "未验证"
                            binding.tvVerify.isClickable = true
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        var json = "null"
                        if (response.errorBody() != null) {
                            try {
                                json = response.errorBody()!!.string()
                                //JSONObject jsonObject = new JSONObject(json);
                                // returncondition = jsonObject.getString("msg");
                                Toast.makeText(context, json, Toast.LENGTH_SHORT).show()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                R.string.network_unknownfailture,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {
                    binding.tvVerify.text = "请检查网络"
                    binding.tvVerify.isClickable = false
                    Toast.makeText(context, R.string.network_loadfailure, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun checkPrivacyState(
        binding: ActivitySecurityBinding
    ) {
        val call = request!!.isUnderSecurity
        binding.stSecurity.visibility = View.INVISIBLE
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    try {
                        val mode = JSONObject(response.body()!!.string())
                        _isPrivacy.value = mode.getBoolean("is_incognito")
                        binding.stSecurity.isChecked = !_isPrivacy.value!!
                        binding.stSecurity.visibility = View.VISIBLE
                    } catch (e: IOException) {
                        Log.d("Privacy", "in this")
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        Log.d("Privacy", "in this")
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    binding.stSecurity.visibility = View.VISIBLE
                    _isPrivacy.value = false
                    binding.stSecurity.isChecked = !_isPrivacy.value!!
                    Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun changePrivacyState(
        state: Boolean
    ) {
        val call =
            request!!.changeSecurityMode(RetrofitManager.API + "auth/update?to_incognito=" + state)
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Log.d("Privacy", "code为: " + response.code())
                    if (response.code() == 400) {
                        ErrorMsg.getErrorMsg(response, context)
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun initNorm() {
        _communityNorm.value = (
                "<font color=\"#00000000\">你好，这里是1037树洞~</font><br>"
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
                        + "<font color=\"#666666\">本规范由1037树洞开发团队PivotStudio 工作室负责解释。树洞运营组联系邮箱：husthole@pivotstudio.cn</font><br><br>"
                )
    }

    fun postShieldWord(
        binding: ItemLabelBinding
    ) {
        val call =
            request!!.addblockword(RetrofitManager.API + "blockwords?word=" + binding.etLabel.text.toString())
        //            val map = HashMap<String, String>()
//            map["word"] = binding.etLabel.text.toString()
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.code() == 200) {
                        var json: String? = null
                        var returncondition: String? = null
                        try {
                            if (response.body() != null) {
                                json = response.body()!!.string()
                            }
                            val jsonObject = JSONObject(json)
                            returncondition = jsonObject.getString("msg")
                            Toast.makeText(context, returncondition, Toast.LENGTH_SHORT).show()
                            _shieldWordList.value!!.add(binding.etLabel.text.toString() + "  ×")
                            binding.apply {
                                labels.setLabels(_shieldWordList.value)
                                tvLabelSheildnumber.text = context!!.getString(R.string.shield_num).format(_shieldWordList.value!!.size, 5)
                                etLabel.setText("")
                                constraintLayout1Label.visibility = View.VISIBLE
                                constraintLayout2Label.visibility = View.INVISIBLE
                                etLabel.isFocusable = true
                                etLabel.isFocusableInTouchMode = true
                                etLabel.requestFocus()
                                val imm = etLabel.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        var json = "null"
                        var returncondition: String? = null
                        if (response.errorBody() != null) {
                            try {
                                json = response.errorBody()!!.string()
                                val jsonObject = JSONObject(json)
                                returncondition = jsonObject.getString("msg")
                                Toast.makeText(context, returncondition, Toast.LENGTH_SHORT).show()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(context, R.string.network_unknownfailture, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {
                    Toast.makeText(
                        context,
                        R.string.network_loadfailure, Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    fun deleteShieldWord(
        text: String,
        dialog: Dialog,
        binding: ItemLabelBinding
    ) {
        val call = request!!.deleteblockword(RetrofitManager.API + "blockwords?word=" + text.substring(0, text.length - 3))
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.code() == 200) {
                        var json: String? = null
                        var returncondition: String? = null
                        dialog.dismiss()
                        try {
                            if (response.body() != null) {
                                json = response.body()!!.string()
                            }
                            val jsonObject = JSONObject(json)
                            returncondition = jsonObject.getString("msg")
                            Toast.makeText(context, returncondition, Toast.LENGTH_SHORT).show()
                            _shieldWordList.value!!.remove(text)
                            binding.labels.setLabels(_shieldWordList.value)
                            binding.tvLabelSheildnumber.text = context!!.getString(R.string.shield_num).format(_shieldWordList.value!!.size, 5)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        dialog.dismiss()
                        var json = "null"
                        var returncondition: String? = null
                        if (response.errorBody() != null) {
                            try {
                                json = response.errorBody()!!.string()
                                val jsonObject = JSONObject(json)
                                returncondition = jsonObject.getString("msg")
                                Toast.makeText(context, returncondition, Toast.LENGTH_SHORT).show()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(context, R.string.network_unknownfailture, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {
                    Toast.makeText(context, R.string.network_loadfailure, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun getShieldList(
        binding: ItemLabelBinding
    ) {
        val call = request!!.blockwords(RetrofitManager.API + "blockwords")
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.code() == 200) {
                        var json = "null"
                        try {
                            if (response.body() != null) {
                                json = response.body()!!.string()
                            }
                            val jsonArray = JSONArray(json)
                            for (i in 0 until jsonArray.length()) {
                                val sonObject2 = jsonArray.getJSONObject(i)
                                _shieldWordList.value!!.add(sonObject2.getString("word") + "  ×")
                            }
                            binding.labels.setLabels(_shieldWordList.value)
                            binding.tvLabelSheildnumber.text = context!!.getString(R.string.shield_num)
                                .format(_shieldWordList.value!!.size, 5)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        var json = "null"
                        var returncondition: String? = null
                        if (response.errorBody() != null) {
                            try {
                                json = response.errorBody()!!.string()
                                val jsonObject = JSONObject(json)
                                returncondition = jsonObject.getString("msg")
                                Toast.makeText(
                                    context, returncondition,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                R.string.network_unknownfailture, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {
                    Toast.makeText(
                        context, R.string.network_loadfailure,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    fun sendEvaluation(
        score: Int,
        binding: FragmentEvaluateBinding
    ) {
        val call = request!!.evaluate(API + "feedback/score?score=" + score.toString())
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Toast.makeText(context, "感谢亲的评分(づ￣3￣)づ╭❤～", Toast.LENGTH_SHORT).show()
                    val chip = (binding.chipGroup.getChildAt(score - 1) as Chip)
//                    Log.d("em",chip.text.toString())
                    chip.isChecked = false
                }
                override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {
                    Toast.makeText(context, R.string.network_loadfailure, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getMineData() {
        val call = request!!.myData() //进行封装
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    try {
                        if (response.body() != null) {
                            val jsonStr = response.body()!!.string()
                            val data = JSONObject(jsonStr)
                            _joinDay.value = data.getInt("join_days")
                            _myHoleNum.value = data.getInt("hole_sum")
                            _myFollowNum.value = data.getInt("follow_num")
                            _myReplyNum.value = data.getInt("replies_num")

                        } else {
                            Log.d("MineJson", "is null");
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
            })
        }
    }

    private fun initNameList() {
        _myNameList.value = listOf(
            R.string.personal_setting,
            R.string.shield_setting,
            R.string.community_norm,
            R.string.share_hole,
            R.string.evaluation_and_suggestions,
            R.string.about_1037,
            R.string.update,
            R.string.login_out
        )
    }

    private fun initViewPager() {
        _myFragmentList.value = listOf(
            MyHoleFollowReplyFragment.newInstance(MyHoleFragmentViewModel.GET_HOLE),
            MyHoleFollowReplyFragment.newInstance(MyHoleFragmentViewModel.GET_FOLLOW),
            MyHoleFollowReplyFragment.newInstance(MyHoleFragmentViewModel.GET_REPLY)
        )
        _myTabTitle.value = listOf(
            R.string.tv_myHoles,
            R.string.tv_myFollows,
            R.string.tv_myReply
        )
        _evalAndAdvFragmentList.value = listOf(
            cn.pivotstudio.modulec.homescreen.ui.fragment.mine.EvaluateFragment.newInstance(),
            AdviceFragment.newInstance()
        )
        _evalAndAdvNameList.value = listOf(
            R.string.eval,
            R.string.adv
        )
    }

    private fun initOption() {
        _mySettingList.value = listOf(
            R.string.campus_email,
            R.string.privacy_security
        )
        _shieldList.value = listOf(
            R.string.keyword_shielding
        )
        _updateList.value = listOf(
            R.string.update_log,
            R.string.check_update
        )
        _shieldWordList.value = mutableListOf()
    }

    companion object {
        const val OTHER_OPTION = 0  //跳转到细分选项
        const val DETAIL = 1    //跳转到详细页面

        const val PERSONAL_SETTING = 0
        const val SHIELD_SETTING = 1
        const val COMMUNITY_NORM = 2
        const val SHARE = 3
        const val EVALUATION_AND_SUGGESTIONS = 4
        const val ABOUT = 5
        const val UPDATE = 6
        const val LOGOUT = 7
    }
}