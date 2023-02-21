package com.xstudy.base.exception;

public class BusinessException extends RuntimeException{
  private final String errMessage;

  public BusinessException(String errMessage) {
    super(errMessage);
    this.errMessage = errMessage;
  }

  public String getErrMessage() {
    return errMessage;
  }

  public static BusinessException info(CommonError commonError){
    return new BusinessException(commonError.getErrMessage());
  }
  public static BusinessException info(String errMessage){
    return new BusinessException(errMessage);
  }

}
