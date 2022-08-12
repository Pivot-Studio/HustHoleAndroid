package cn.pivotstudio.husthole.moduleb.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @classname:BaseResponse
 * @description:
 * @date:2022/4/29 14:43
 * @version:1.0
 * @author:
 */
public class BaseResponse {

    //返回码
    @SerializedName("res_code")
    @Expose
    public Integer responseCode;

    //返回的错误信息
    @SerializedName("res_error")
    @Expose
    public String responseError;
}
