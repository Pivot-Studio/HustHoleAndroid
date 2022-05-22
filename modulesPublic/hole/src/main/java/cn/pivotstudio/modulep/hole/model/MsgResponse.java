package cn.pivotstudio.modulep.hole.model;

/**
 * @classname:MsgResponse
 * @description:
 * @date:2022/5/8 23:22
 * @version:1.0
 * @author:
 */
public class MsgResponse {
    String msg;
    private String model;

    public String getModel() {
        if(model==null) {model="BASE";}
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
