package com.xstudy.base.model;

import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class RestResponse<T> {

  /** 响应编码,0为正常,-1错误 */
  private int code;

  /** 响应提示信息 */
  private String msg;

  /** 响应内容 */
  private T result;

  public RestResponse() {
    this(0, "success");
  }

  public RestResponse(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static <T> RestResponse<T> validFail(String msg) {
    RestResponse<T> response = new RestResponse<>();
    response.setCode(-1);
    response.setMsg(msg);
    return response;
  }

  public static <T> RestResponse<T> validFail(T result, String msg) {
    RestResponse<T> response = new RestResponse<>();
    response.setCode(-1);
    response.setResult(result);
    response.setMsg(msg);
    return response;
  }

  public static <T> RestResponse<T> success(T result) {
    RestResponse<T> response = new RestResponse<>();
    response.setResult(result);
    return response;
  }

  public static <T> RestResponse<T> success(T result, String msg) {
    RestResponse<T> response = new RestResponse<>();
    response.setResult(result);
    response.setMsg(msg);
    return response;
  }

  public static <T> RestResponse<T> success() {
    return new RestResponse<>();
  }

  public Boolean isSuccessful() {
    return this.code == 0;
  }
}
