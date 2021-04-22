package yq.bean;

import lombok.Data;

//响应前端的javabean,controller将javabean转成json,json有data字段
@Data
public class Result {
    private Object data;
    private Integer code;
    private String message;

    public static Result success(Object data){
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public  static Result fail(){
        Result result = new Result();
        result.setCode(500);
        result.setMessage("fail");
        result.setData(null);
        return result;
    }
}
