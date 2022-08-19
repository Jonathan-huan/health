package com.hzh.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hzh.constant.MessageConstant;
import com.hzh.constant.RedisMessageConstant;
import com.hzh.entity.Result;
import com.hzh.pojo.Order;
import com.hzh.pojo.OrderSetting;
import com.hzh.service.OrderService;
import com.hzh.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    //在线体检预约
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
        String telephone =(String) map.get("telephone");
        //从Redist中获取保存的验证码
        String codeInRedis=jedisPool.getResource().get(telephone+ RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode=(String)map.get("validateCode");
        //将用户输入的验证码和Redis中保存的验证码进行比对
        if(codeInRedis!=null&&validateCode!=null&&validateCode.equals(codeInRedis)){
            //如果比对成功，调用服务完成预约的业务处理
            map.put("orderType", Order.ORDERTYPE_WEIXIN);//设置预约类型，分为微信预约和电话预约
            Result result=null;
            try{
                result=orderService.order(map);
            }catch (Exception e){
                e.printStackTrace();
                return result;
            }
            if(result.isFlag()){
                //预约成功，可以为用户发送短信
                try{
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,(String) map.get("orderDate"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return result;
        }else{
            //如果比对不成功，返回结果给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try{
            Map map=orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
