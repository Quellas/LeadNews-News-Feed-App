package com.itheima.common.exception;

import com.itheima.common.exception.LeadNewsException;
import com.itheima.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/8 09:54
 * @description 标题
 * @package com.itheima.common.exception
 */
@RestControllerAdvice//标识该类是一个全局的异常处理类
public class GlobalExceptionHandler {

    private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //写一个方法 用来专门处理controller抛出的[系统的]异常信息捕获
    //@ExceptionHandler(value=Exception.class) 修饰方法 用来当controller 的异常发生的异常是 value指定的异常的 时候
    //执行 被他修饰的方法
    @ExceptionHandler(value=Exception.class)
    public Result handlerSystemExecption(Exception e){
        //e.printStackTrace();//异常打印堆栈  上线之前要删除 打印日志
        logger.error("sytem exception error:",e);
        return Result.errorMessage(e.getMessage());//为了开发方便，将来要改成：网络异常
    }
    //写一个方法 用来专门处理controller抛出的[业务 自定义的异常类的]异常信息捕获
    @ExceptionHandler(value= LeadNewsException.class)
    public Result handlerBussinessExecption(LeadNewsException e){
        //e.printStackTrace();//异常打印堆栈
        logger.error("Bussiness exception error:",e);
        return Result.errorMessage(e.getMessage());//业务的异常 一定要使用自定义的message
    }
}
