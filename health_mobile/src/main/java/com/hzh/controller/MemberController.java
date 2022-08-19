package com.hzh.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hzh.constant.MessageConstant;
import com.hzh.constant.RedisMessageConstant;
import com.hzh.entity.Result;
import com.hzh.pojo.Member;
import com.hzh.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;
    //手机号快速登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map){
        String telephone = (String) map.get("telephone");
        String validateCode=(String) map.get("validateCode");
        //从redis中获取保存的验证码
        String codeInRedis=jedisPool.getResource().get(telephone+ RedisMessageConstant.SENDTYPE_LOGIN);
        if(codeInRedis!=null&&validateCode!=null&&validateCode.equals(codeInRedis)){
            Member member = memberService.findByTelephone(telephone);
            if(member==null){
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                memberService.add(member);
            }
            Cookie cookie=new Cookie("login_member_telephone",telephone);
            cookie.setPath("/");
            cookie.setMaxAge(60*60*24*30);
            response.addCookie(cookie);

            //保存会员信息到redis中
            String json= JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone,60*30,json);

            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        }
        return new Result(false,MessageConstant.VALIDATECODE_ERROR);
    }
}
