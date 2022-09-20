package cn.pivotstudio.modulec.homescreen.viewmodel

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
import cn.pivotstudio.moduleb.libbase.constant.Constant.CONSTANT_STANDARD_LOAD_SIZE
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineReplyBinding
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager
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
 *@classname MyHoleFollowReplyFragment
 * @description:
 * @date :2022/9/12 17:44
 * @version :1.0
 * @author
 */
const val GET_HOLE = 1
const val GET_FOLLOW = 2
const val GET_REPLY = 3

class MyHoleFragmentViewModel : ViewModel() {
    private val BASE_URL = RetrofitManager.API

    private val _myHoleStartId = MutableLiveData<Int>()
    private val _myFollowStartId = MutableLiveData<Int>()
    private val _myReplyStartId = MutableLiveData<Int>()
    private val _listSize = MutableLiveData<Int>()
    private val _myHolesList = MutableLiveData<ArrayList<Array<String?>>>()
    private val _myFollowList = MutableLiveData<ArrayList<Array<String?>>>()
    private val _myReplyList = MutableLiveData<ArrayList<Array<String?>>>()

    val myHoleStartId: LiveData<Int> = _myHoleStartId
    val myFollowStartId: LiveData<Int> = _myFollowStartId
    val myReplyStartId: LiveData<Int> = _myReplyStartId
    val listSize: LiveData<Int> = _listSize
    val myHolesList: LiveData<ArrayList<Array<String?>>> = _myHolesList
    val myFollowList: LiveData<ArrayList<Array<String?>>> = _myFollowList
    val myReplyList: LiveData<ArrayList<Array<String?>>> = _myReplyList

    private var retrofit: Retrofit? = null
    private var request: RequestInterface? = null
    private var jsonArray: JSONArray? = null

    init {
        initMyHoleRefresh()
        initMyFollowRefresh()
        initMyReplyRefresh()

        _listSize.value = CONSTANT_STANDARD_LOAD_SIZE

        RetrofitManager.RetrofitBuilder(BASE_URL)
        retrofit = RetrofitManager.getRetrofit()
        request = retrofit!!.create(RequestInterface::class.java)

        getMyHoleList()
        getMyFollowList()
        getMyReplyList()
    }

    fun initMyHoleRefresh() {
//        _myHolesList.value?.clear()
        _myHolesList.value = ArrayList()
        _myHoleStartId.value = 0
    }

    fun initMyFollowRefresh() {
//        _myFollowList.value?.clear()
        _myFollowList.value = ArrayList()
        _myFollowStartId.value = 0
    }

    fun initMyReplyRefresh() {
//        _myReplyList.value?.clear()
        _myReplyList.value = ArrayList()
        _myReplyStartId.value = 0
    }

    fun getMyHoleList() {
        viewModelScope.launch {
            val call = request!!.myHoles(_myHoleStartId.value!!, _listSize.value!!)
            _myHoleStartId.value = _myHoleStartId.value!! + _listSize.value!!   //每次更新起始位置也相应更新
            Log.e("hh", _myHoleStartId.value.toString())
            dataRequest(call, GET_HOLE)
        }
    }

    fun getMyFollowList() {
        viewModelScope.launch {
            val call = request!!.myFollow(_myFollowStartId.value!!, _listSize.value!!)
            _myFollowStartId.value = _myFollowStartId.value!! + _listSize.value!!

            dataRequest(call, GET_FOLLOW)
        }
    }

    fun getMyReplyList() {
        viewModelScope.launch {
            val call = request!!.myReplies(_myReplyStartId.value!!, _listSize.value!!)
            _myReplyStartId.value = _myReplyStartId.value!! + _listSize.value!!

            dataRequest(call, GET_REPLY)
        }
    }

    private fun dataRequest(call: Call<ResponseBody>, type: Int) {
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                lateinit var newList: MutableList<Array<String?>>
                when (type) {
                    GET_HOLE -> newList = _myHolesList.value!!.toMutableList()
                    GET_FOLLOW -> newList = _myFollowList.value!!.toMutableList()
                    GET_REPLY -> newList = _myReplyList.value!!.toMutableList()
                }
                var json = "null"
                try {
                    if (response.body() != null) {
                        json = response.body()!!.string()
                    }
                    jsonArray = JSONArray(json)
                    try {
                        for (f in 0 until jsonArray!!.length()) {
                            val sonObject = jsonArray!!.getJSONObject(f)
                            val singleHole = arrayOfNulls<String>(7)
                            singleHole[1] = sonObject.getString("content")
                            singleHole[2] = sonObject.getString("created_timestamp")
                            singleHole[3] = sonObject.getInt("hole_id").toString() + ""
                            if (type == GET_HOLE || type == GET_FOLLOW) {
                                singleHole[4] = sonObject.getInt("follow_num").toString() + ""
                                singleHole[5] = sonObject.getInt("reply_num").toString() + ""
                                singleHole[6] = sonObject.getInt("thumbup_num").toString() + ""
                            }
                            else if(type == GET_REPLY) {
                                singleHole[4] = sonObject.getString("alias").toString() + ""
                                singleHole[5] = sonObject.getString("hole_content").toString() + ""
                                singleHole[6] = sonObject.getInt("local_reply_id").toString() + ""
                            }
                            newList.add(singleHole)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                when (type) {
                    GET_HOLE -> _myHolesList.value = newList as ArrayList<Array<String?>>
                    GET_FOLLOW -> _myFollowList.value = newList as ArrayList<Array<String?>>
                    GET_REPLY -> _myReplyList.value = newList as ArrayList<Array<String?>>
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {}
        })
    }

    fun deleteHole(
        dialog: Dialog,
        binding: ItemMineReplyBinding,
        content: Context?,
        position: Int,
    ){

        val call = request!!.delete_hole_2(BASE_URL+ "replies/" + _myReplyList.value!![position][3] + "/" +_myReplyList.value!![position][6])

        viewModelScope.launch {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    binding.myReplyDelete.visibility = View.GONE
                    dialog.dismiss()
                    if (response.code() == 200) {
                        var json = "null"
                        var returncondition: String? = null
                        if (response.body() != null) {
                            try {
                                json = response.body()!!.string()
                                val jsonObject = JSONObject(json)
                                returncondition = jsonObject.getString("msg")
                                Toast.makeText(
                                    content, returncondition,
                                    Toast.LENGTH_SHORT
                                ).show()
                                _myReplyList.value?.removeAt(position)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(
                                content, "删除失败，超过可删除的时间范围",
                                Toast.LENGTH_SHORT
                            ).show()
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
                                    content, returncondition,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(
                                content,
                                R.string.network_unknownfailture,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                    Toast.makeText(
                        content,
                        R.string.network_deletefailture, Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
