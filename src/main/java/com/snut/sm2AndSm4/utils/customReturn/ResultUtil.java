package com.snut.sm2AndSm4.utils.customReturn;

/**
 * Created with IntelliJ IDEA.
 *
 * @Package: com.snut.tomorrowNews.common
 * @ClassName: ResultUtil
 * @Description: 自定义返回信息
 * @Version: 1.0
 */
public class ResultUtil {

    /**
     * 请求成功
     */
    public static <T> BaseResult<T> success(T data) {
        return commonResult(1, 200, "请求成功", data);
    }

    /**
     * 请求成功但是数据错误  比如学生找不到
     */
    public static <T> BaseResult<T> error(String errorMsg) {
        return error(200, errorMsg);
    }

    /**
     * 请求失败自定义返回信息
     */
    public static <T> BaseResult<T> error(Integer code, String errorMsg) {
        return commonResult(0, code, errorMsg, null);
    }

    /**
     * 自定义返回
     */
    private static <T> BaseResult<T> commonResult(Integer status, Integer code, String errMsg, T data) {
        BaseResult<T> result = new BaseResult<>();
        result.setStatus(status);
        result.setCode(code);
        result.setMsg(errMsg);
        result.setData(data);
        return result;
    }
}

