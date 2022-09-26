package cn.pivotstudio.modulec.homescreen.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ActivitySecurityBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineOthersBinding
import cn.pivotstudio.modulec.homescreen.oldversion.network.ErrorMsg
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager
import cn.pivotstudio.modulec.homescreen.ui.fragment.MyHoleFollowReplyFragment
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
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
                            Toast.makeText(context, R.string.network_unknownfailture, Toast.LENGTH_SHORT).show()
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
        val call = request!!.changeSecurityMode(RetrofitManager.API + "auth/update?to_incognito=" + state)
        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Log.d("Privacy", "code为 ： " + response.code())
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