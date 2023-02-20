package com.xstudy.base.exception.handler;

import com.xstudy.base.exception.BusinessException;
import com.xstudy.base.exception.CommonError;
import com.xstudy.base.exception.RestErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public RestErrorResponse customException(BusinessException e) {
    log.error("【系统异常】{}", e.getErrMessage(), e);
    return new RestErrorResponse(e.getErrMessage());
  }

  @ResponseBody
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public RestErrorResponse doValidException(
      MethodArgumentNotValidException argumentNotValidException) {

    BindingResult bindingResult = argumentNotValidException.getBindingResult();
    StringBuilder errMsg = new StringBuilder();

    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    fieldErrors.forEach(error -> errMsg.append(error.getDefaultMessage()).append(","));
    log.error(errMsg.toString());
    return new RestErrorResponse(errMsg.toString());
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public RestErrorResponse exception(Exception e) {

    log.error("【系统异常】{}", e.getMessage(), e);

    return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
  }
}
