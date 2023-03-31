package com.allen.message.forwarding.advice;


import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.ResultStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller层异常通用处理类
 *
 * @author allen
 * @date 2021年5月19日
 * @since 1.0.0
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理约束异常
     *
     * @param e 异常对象
     * @return 异常转换结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResult<List<String>> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder errMsg = new StringBuilder();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errMsg.append(violation.getMessage()).append(";");
        }
        log.error("参数校验失败，失败信息：" + errMsg, e);
        return BaseResult.result(ResultStatus.PARAM_ERROR.getCode(), errMsg.toString());
    }

    /**
     * 处理参数校验异常
     *
     * @param e 异常对象
     * @return 异常转换结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResult<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errMsg.append(fieldError.getDefaultMessage()).append(";");
        }
        log.error("参数校验失败，失败信息：" + errMsg, e);
        return BaseResult.result(ResultStatus.PARAM_ERROR.getCode(), errMsg.toString());
    }

    /**
     * 处理参数校验异常
     *
     * @param e 异常对象
     * @return 异常转换结果
     */
    @ExceptionHandler(BindException.class)
    public BaseResult<List<String>> handleBindException(BindException e) {
        StringBuilder errMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errMsg.append(fieldError.getDefaultMessage()).append(";");
        }
        log.error("参数校验失败，失败信息：" + errMsg, e);
        return BaseResult.result(ResultStatus.PARAM_ERROR.getCode(), errMsg.toString());
    }

    /**
     * 处理自定义公共业务异常
     *
     * @param e 异常对象
     * @return 异常转换结果
     */
    @ExceptionHandler(CustomBusinessException.class)
    public BaseResult<Object> handleCustomBusinessException(CustomBusinessException e) {
        log.error("发生业务异常，异常编码：{}，异常信息：{}", e.getCode(), e.getMessage(), e);
        return new BaseResult<Object>(e.getCode(), e.getMessage());
    }

    /**
     * 处理未知异常
     *
     * @param e 异常对象
     * @return 异常转换结果
     */
    @ExceptionHandler(Exception.class)
    public BaseResult<Object> handleUnknownException(Exception e) {
        log.error("发生系统未知异常", e);
        return BaseResult.systemError((Object) null);
    }

}