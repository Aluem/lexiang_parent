package top.lexiang.common.entity;

/**
 * 公共的返回码
 *  返回码：
 *      成功：20000
 *      失败：20001
 *      未登录：20002
 *      未授权：20003
 *      抛出异常：99999
 */
public enum ResultCode {

    SUCCESS(20000,true,"操作成功"),
    LOGINSUCCESS(20000,true,"登录成功"),
    VALUEISEMPTY(20000,true,"没有数据"),
    PASSWORDERROR(20001,false,"用户名或密码错误"),
    FAIL(20001,false,"操作失败"),
    TARGETISEMPTY(20001,false,"操作对象不存在"),
    INSERTVALUEISEMPTY(20001,false,"传入参数不正确"),
    UNAUTHENTICATED(20002,false,"您还没登陆"),
    UNAUTHORISE(20003,false,"权限不足"),
    SERVER_ERROR(99999,false,"抱歉，系统繁忙，请稍后重试");

    //提供构造参数，化简返回数据
    int code;
    boolean success;
    String message;

    ResultCode(int code, boolean success, String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
