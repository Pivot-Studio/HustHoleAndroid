package cn.pivotstudio.modulec.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.util.ArrayList

/**
 *@classname MyHoleFragment.kt
 * @description:
 * @date :2022/9/12 17:44
 * @version :1.0
 * @author
 */


class MyHoleFragmentViewModel : ViewModel() {
    private val BASE_URL = RetrofitManager.API
    private val _startId = MutableLiveData<Int>()
    private val _listSize = MutableLiveData<Int>()
    private val _myHolesList = MutableLiveData<ArrayList<Array<String?>>>()


    val startId: LiveData<Int> = _startId
    val listSize: LiveData<Int> = _listSize
    val myHolesList: LiveData<ArrayList<Array<String?>>> = _myHolesList

    private var retrofit: Retrofit? = null
    private var request: RequestInterface? = null
    private var jsonArray: JSONArray? = null

    init {
        _startId.value = 0;
        _listSize.value = 10
        _myHolesList.value = ArrayList<Array<String?>>()

        RetrofitManager.RetrofitBuilder(BASE_URL)
        retrofit = RetrofitManager.getRetrofit()
        request = retrofit!!.create(RequestInterface::class.java)

        getMyHoleList()
    }

    fun getMyHoleList() {
        viewModelScope.launch {
            val call = request!!.myHoles(_startId.value!!, _listSize.value!!)
            _startId.value!!.plus(_listSize.value!!)     //每次更新起始位置也相应更新
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    var json = "null"
                    try {
                        if (response.body() != null) {
                            json = response.body()!!.string()
                            Log.d("myHole", "this is myHoles reply: $json")
                        }
                        jsonArray = JSONArray(json)
                        try {
                            for (f in 0 until jsonArray!!.length()) {
                                val sonObject = jsonArray!!.getJSONObject(f)
                                val SingleHole = arrayOfNulls<String>(9)
                                SingleHole[1] = sonObject.getString("content")
                                SingleHole[2] = sonObject.getString("created_timestamp")
                                SingleHole[3] = sonObject.getInt("follow_num").toString() + ""
                                SingleHole[4] = sonObject.getInt("hole_id").toString() + ""
                                SingleHole[5] = sonObject.getBoolean("is_follow").toString() + ""
                                SingleHole[6] = sonObject.getBoolean("is_thumbup").toString() + ""
                                SingleHole[7] = sonObject.getInt("reply_num").toString() + ""
                                SingleHole[8] = sonObject.getInt("thumbup_num").toString() + ""
//                                SingleHole[9] = sonObject.getString("forest_name").toString() + ""
//                                SingleHole[10] = sonObject.getString("role").toString() + ""
                                val newList = _myHolesList.value!!.toMutableList()
                                newList.add(SingleHole)
                                _myHolesList.value = newList as ArrayList<Array<String?>>
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {}
            })
        }
    }

    fun initRefresh() {
        _myHolesList.value?.clear()
        _startId.value = 0
    }
}
