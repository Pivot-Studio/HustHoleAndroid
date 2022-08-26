package cn.pivotstudio.modulec.homescreen.oldversion.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineBinding
import cn.pivotstudio.modulec.homescreen.oldversion.mine.*
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import com.alibaba.android.arouter.launcher.ARouter
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class MineFragment : Fragment() {

    private lateinit var binding: FragmentMineBinding

    private var tv_joinDays: TextView? = null
    private var tv_myStarNum: TextView? = null
    private var tv_myHoleNum: TextView? = null
    private var tv_myReplyNum: TextView? = null
    private var ppwShare: PopupWindow? = null
    var retrofit: Retrofit? = null
    var request: RequestInterface? = null
    var TAG = "isMine"
    var flag = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false)
        val rootView = inflater.inflate(R.layout.fragment_mine, container, false)
        val settings = rootView.findViewById<RelativeLayout>(R.id.settings)
        val shield = rootView.findViewById<RelativeLayout>(R.id.shield)
        val rules = rootView.findViewById<RelativeLayout>(R.id.rules)
        val share = rootView.findViewById<RelativeLayout>(R.id.share)
        val evaluate = rootView.findViewById<RelativeLayout>(R.id.evaluateAndAdvice)
        val about = rootView.findViewById<RelativeLayout>(R.id.about)
        val update = rootView.findViewById<RelativeLayout>(R.id.update)
        val logout = rootView.findViewById<RelativeLayout>(R.id.logout)
        val myHole = rootView.findViewById<ConstraintLayout>(R.id.my_hole)
        val myStar = rootView.findViewById<ConstraintLayout>(R.id.my_star)
        val myReply = rootView.findViewById<ConstraintLayout>(R.id.my_reply)
        tv_joinDays = rootView.findViewById(R.id.my_date)
        tv_myHoleNum = rootView.findViewById(R.id.my_hole_num)
        tv_myStarNum = rootView.findViewById(R.id.my_star_num)
        tv_myReplyNum = rootView.findViewById(R.id.my_reply_num)
        val location = rootView.findViewById<TextView>(R.id.ppw_share_location)
        val shareCardView = LayoutInflater.from(context).inflate(R.layout.ppw_share, null)
        // backgroundView = LayoutInflater.from(getContext()).inflate(R.layout.ppw_share_card_darkscreen, null);
        val shareCard = shareCardView.findViewById<LinearLayout>(R.id.share_card)
        val cancel = shareCardView.findViewById<TextView>(R.id.share_cancel_button)
        ppwShare = PopupWindow(shareCardView)
        ppwShare!!.width = ViewGroup.LayoutParams.MATCH_PARENT
        ppwShare!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // ppwShare.setAnimationStyle(R.style.Page2Anim);

        //ppwBackground=new PopupWindow(backgroundView);
        // ppwBackground.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // ppwBackground.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //ppwBackground.setAnimationStyle(R.style.darkScreenAnim);
        RetrofitManager.RetrofitBuilder(BASE_URL)
        retrofit = RetrofitManager.getRetrofit()
        request = retrofit!!.create(RequestInterface::class.java)
        myData
        settings.setOnClickListener { view: View -> onClick(view) }
        shield.setOnClickListener { view: View -> onClick(view) }
        rules.setOnClickListener { view: View -> onClick(view) }
        share.setOnClickListener { view: View -> onClick(view) }
        evaluate.setOnClickListener { view: View -> onClick(view) }
        about.setOnClickListener { view: View -> onClick(view) }
        update.setOnClickListener { view: View -> onClick(view) }
        logout.setOnClickListener { view: View -> onClick(view) }
        myHole.setOnClickListener { view: View -> onClick(view) }
        myStar.setOnClickListener { view: View -> onClick(view) }
        myReply.setOnClickListener { view: View -> onClick(view) }
        cancel.setOnClickListener { v: View? ->
            ppwShare!!.dismiss()
            flag = 0
            cancelDarkBackGround()
        }
        shareCard.setOnClickListener { v: View? ->
            ppwShare!!.dismiss()
            flag = 0
            cancelDarkBackGround()
            val intent = Intent(activity, ShareCardActivity::class.java)
            startActivity(intent)
        }
        myData
        return rootView
    }

    fun cancelDarkBackGround() {
        val lp = requireActivity().window.attributes
        lp.alpha = 1f // 0.0~1.0
        requireActivity().window.attributes = lp
    }//Log.d(TAG,"is null");//Log.d(TAG,"已登陆");//  Log.d(TAG,joinDays+"");

    //进行封装
    private val myData: Unit
        private get() {
            Thread {
                val call = request!!.myData() //进行封装
                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        try {
                            if (response.errorBody() != null) {
                            }
                            if (response.body() != null) {
                                val jsonStr = response.body()!!.string()
                                val data = JSONObject(jsonStr)
                                val joinDays = data.getInt("join_days")
                                val myHoleNum = data.getInt("hole_sum")
                                val myStarNum = data.getInt("follow_num")
                                val myReplyNum = data.getInt("replies_num")
                                //  Log.d(TAG,joinDays+"");
                                var days = "我来到树洞已经<font color=\"#02A9F5\">$joinDays</font>天啦。"
                                if (CheckingToken.IfTokenExist()) {
                                    //Log.d(TAG,"已登陆");
                                    tv_joinDays!!.text = Html.fromHtml(days)
                                    tv_myHoleNum!!.text = myHoleNum.toString()
                                    tv_myStarNum!!.text = myStarNum.toString()
                                    tv_myReplyNum!!.text = myReplyNum.toString()
                                } else {
                                    days = "我来到树洞已经<font color=\"#02A9F5\">" + 0 + "</font>天啦。"
                                    tv_joinDays!!.text = Html.fromHtml(days)
                                    tv_myHoleNum!!.text = 0.toString()
                                    tv_myStarNum!!.text = 0.toString()
                                    tv_myReplyNum!!.text = 0.toString()
                                }
                            } else {
                                //Log.d(TAG,"is null");
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
                })
            }.start()
        }

    fun onClick(view: View) {
        val intent: Intent
        var id = 0
        if (flag == 0) {
            id = view.id
        }
        if (id == R.id.my_hole) {
            if (CheckingToken.IfTokenExist()) {
                intent = Intent(
                    requireActivity().applicationContext,
                    HoleStarReplyActivity::class.java
                )
                intent.putExtra("initFragmentId", 0)
                startActivity(intent)
            } else {
                // intent = new Intent(getActivity(), EmailVerifyActivity.class);
                // startActivity(intent);
            }
        } else if (id == R.id.my_star) {
            if (CheckingToken.IfTokenExist()) {
                intent = Intent(
                    requireActivity().applicationContext,
                    HoleStarReplyActivity::class.java
                )
                intent.putExtra("initFragmentID", 1)
                startActivity(intent)
            } else {
            }
        } else if (id == R.id.my_reply) {
            if (CheckingToken.IfTokenExist()) {
                intent = Intent(
                    requireActivity().applicationContext,
                    HoleStarReplyActivity::class.java
                )
                intent.putExtra("initFragmentID", 2)
                startActivity(intent)
            } else {
                //                intent = new Intent(getActivity(), EmailVerifyActivity.class);
                //                startActivity(intent);
            }
        } else if (id == R.id.settings) {
            if (CheckingToken.IfTokenExist()) {
                intent = Intent(requireActivity().applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            } else {
                //                intent = new Intent(getActivity(), EmailVerifyActivity.class);
                //                startActivity(intent);
            }
        } else if (id == R.id.shield) {
            intent = Intent(requireActivity().applicationContext, ScreenActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.rules) {
            intent = Intent(requireActivity().applicationContext, RulesActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.share) {
            flag = 1
            val lp = requireActivity().window.attributes
            lp.alpha = 0.6f // 0.0~1.0
            requireActivity().window.attributes = lp
            //  ppwBackground.showAsDropDown(rootView);
            ppwShare!!.showAtLocation(
                requireActivity().window.decorView, Gravity.BOTTOM, 0,
                0
            )
        } else if (id == R.id.evaluateAndAdvice) {
            if (CheckingToken.IfTokenExist()) {
                intent = Intent(
                    requireActivity().applicationContext,
                    EvaluateAndAdviceActivity::class.java
                )
                intent.putExtra("initFragmentId", 0)
                startActivity(intent)
            } else {
                //                intent = new Intent(getActivity(), EmailVerifyActivity.class);
                //                startActivity(intent);
            }
        } else if (id == R.id.about) {
            intent = Intent(requireActivity().applicationContext, AboutActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.update) {
            intent = Intent(requireActivity().applicationContext, DetailUpdateActivity::class.java)
            startActivity(intent)
            /*case R.id.update2:
                intent = new Intent(getActivity().getApplicationContext(), Main3Activity.class);
                startActivity(intent);
                break;

             */
        } else if (id == R.id.logout) {
            val dialog = Dialog(requireContext())
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_logout, null)
            dialog.setContentView(dialogView)
            val btn_cancel = dialogView.findViewById<Button>(R.id.cancel)
            val btn_logout = dialogView.findViewById<Button>(R.id.logout)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            btn_cancel.setOnClickListener { dialog.dismiss() }
            btn_logout.setOnClickListener {
                dialog.dismiss()
                val mmkvUtil = MMKVUtil.getMMKV(context)
                mmkvUtil.put(Constant.USER_TOKEN, "")
                mmkvUtil.put(Constant.USER_TOKEN_V2, "")
                mmkvUtil.put(Constant.IS_LOGIN, false)
                ARouter.getInstance().build("/loginAndRegister/LARActivity").navigation()
                (context as HomeScreenActivity?)!!.finish()
            }
            dialog.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private val BASE_URL = RetrofitManager.API
    }
}