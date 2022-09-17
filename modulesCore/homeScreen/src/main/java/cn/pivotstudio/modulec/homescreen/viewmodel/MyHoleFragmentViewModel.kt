package cn.pivotstudio.modulec.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.pivotstudio.moduleb.libbase.constant.Constant.CONSTANT_STANDARD_LOAD_SIZE
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.MyStarFragment
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

/**
 *@classname MyHoleFragment.kt
 * @description:
 * @date :2022/9/12 17:44
 * @version :1.0
 * @author
 */
private const val GET_HOLE = 1
private const val GET_FOLLOW = 2

class MyHoleFragmentViewModel : ViewModel() {
    private val BASE_URL = RetrofitManager.API
    private val _myHoleStartId = MutableLiveData<Int>()
    private val _myFollowStartId = MutableLiveData<Int>()
    private val _listSize = MutableLiveData<Int>()
    private val _myHolesList = MutableLiveData<ArrayList<Array<String?>>>()
    private val _myFollowList = MutableLiveData<ArrayList<Array<String?>>>()


    val myHoleStartId: LiveData<Int> = _myHoleStartId
    val myFollowStartId: LiveData<Int> = _myFollowStartId
    val listSize: LiveData<Int> = _listSize
    val myHolesList: LiveData<ArrayList<Array<String?>>> = _myHolesList
    val myFollowList: LiveData<ArrayList<Array<String?>>> = _myFollowList

    private var retrofit: Retrofit? = null
    private var request: RequestInterface? = null
    private var jsonArray: JSONArray? = null

    init {
        _myHoleStartId.value = 0;
        _myFollowStartId.value = 0;
        _listSize.value = CONSTANT_STANDARD_LOAD_SIZE
        _myHolesList.value = ArrayList()
        _myFollowList.value = ArrayList()

        RetrofitManager.RetrofitBuilder(BASE_URL)
        retrofit = RetrofitManager.getRetrofit()
        request = retrofit!!.create(RequestInterface::class.java)

        getMyHoleList()
        getMyFollowList()
    }

    private fun dataRequest(call: Call<ResponseBody>, type: Int){
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                lateinit var newList: MutableList<Array<String?>>
                if (type == GET_HOLE)
                    newList = _myHolesList.value!!.toMutableList()
                else if(type == GET_FOLLOW)
                    newList = _myFollowList.value!!.toMutableList()
                var json = "null"
                try {
                    if (response.body() != null) {
                        json = response.body()!!.string()
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
                            newList.add(SingleHole)

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if(type == GET_HOLE)
                    _myHolesList.value = newList as ArrayList<Array<String?>>
                else if(type == GET_FOLLOW)
                    _myFollowList.value = newList as ArrayList<Array<String?>>
            }

            override fun onFailure(call: Call<ResponseBody?>, tr: Throwable) {}
        })
    }

    fun getMyHoleList() {
        viewModelScope.launch {
            val call = request!!.myHoles(_myHoleStartId.value!!, _listSize.value!!)
            _myHoleStartId.value = _myHoleStartId.value!! + _listSize.value!!   //每次更新起始位置也相应更新
            Log.e("hh",_myHoleStartId.value.toString())
            dataRequest(call, GET_HOLE)
        }
    }

    fun initMyHoleRefresh() {
        _myHolesList.value?.clear()
        _myHoleStartId.value = 0
    }

    fun initMyFollowRefresh() {
        _myFollowList.value?.clear()
        _myFollowStartId.value = 0
    }

    fun getMyFollowList() {
        viewModelScope.launch {
            val call = request!!.myFollow(_myFollowStartId.value!!, _listSize.value!!)
            _myFollowStartId.value = _myFollowStartId.value!! + _listSize.value!!

            dataRequest(call, GET_FOLLOW)
        }
    }
}
