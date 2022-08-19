package com.hzh.controller;

import com.aliyuncs.exceptions.ClientException;
import com.hzh.constant.MessageConstant;
import com.hzh.constant.RedisConstant;
import com.hzh.constant.RedisMessageConstant;
import com.hzh.entity.Result;
import com.hzh.utils.SMSUtils;
import com.hzh.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;

    //体检预约时发送手机验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        try{
            //发送短信
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());

        }catch (ClientException e){
            e.printStackTrace();
            //验证码发送失败
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }finally {
            System.out.println("发送的手机验证码为："+code);
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,300,code.toString());
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
    }

    //登录时发送手机验证码
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        try{
            //发送短信
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());

        }catch (ClientException e){
            e.printStackTrace();
            //验证码发送失败
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }finally {
            System.out.println("发送的手机验证码为："+code);
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,300,code.toString());
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
    }
}
