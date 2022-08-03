package cn.pivotstudio.modulec.loginandregister.model;

/**
 * @classname:ForgetPasswordResponse
 * @description:登录界面
 * @date:2022/5/1 22:36
 * @version:1.0
 * @author:
 */
public class LoginResponse {

    private String msg;
    private String token;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}