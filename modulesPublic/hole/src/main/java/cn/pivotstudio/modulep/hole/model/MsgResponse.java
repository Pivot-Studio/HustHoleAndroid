package cn.pivotstudio.modulep.hole.model;

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
