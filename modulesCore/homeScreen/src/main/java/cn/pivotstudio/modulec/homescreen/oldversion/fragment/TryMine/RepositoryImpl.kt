package cn.pivotstudio.modulec.homescreen.oldversion.fragment.TryMine

import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RepositoryImpl : Repository{

    var request: RequestInterface? = null
    var retrofit: Retrofit? = null

    override suspend fun getMyData() : MyData {
        RetrofitManager.RetrofitBuilder(BASE_URL)
        retrofit = RetrofitManager.getRetrofit()
        request = retrofit?.create(RequestInterface::class.java)
        val call = request?.myData()
        var myData = MyData()
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    response.body()?.let {
                        val jsonStr = response.body()!!.string()
                        val data = JSONObject(jsonStr)
                        myData = MyData(
                            joinDays = data.getInt("join_days"),
                            holeNum = data.getInt("hole_sum"),
                            starNum = data.getInt("follow_num"),
                            replyNum = data.getInt("replies_num")
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
        return myData
    }

    companion object{
        val BASE_URL: String= RetrofitManager.API

        @Volatile
        private var instance: RepositoryImpl? = null

        fun getInstance() =
            this.instance ?: synchronized(this) {
                instance ?: RepositoryImpl().also {
                    instance = it
                }
            }
    }
}

