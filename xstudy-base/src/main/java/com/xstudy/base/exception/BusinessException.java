package com.xstudy.base.exception;

public class BusinessException extends RuntimeException{
  private static final long serialVersionUID = 5565760508056698922L;
  private final String errMessage;

  public BusinessException(String errMessage) {
    super(errMessage);
    this.errMessage = errMessage;
  }

  public String getErrMessage() {
    return errMessage;
  }

  public static void cast(CommonError commonError){
    throw new BusinessException(commonError.getErrMessage());
  }
  public static void cast(String errMessage){
    throw new BusinessException(errMessage);
  }

}
