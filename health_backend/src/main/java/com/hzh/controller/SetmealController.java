package com.hzh.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hzh.constant.MessageConstant;
import com.hzh.constant.RedisConstant;
import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.entity.Result;
import com.hzh.pojo.Setmeal;
import com.hzh.service.SetmealService;
import com.hzh.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //使用JedisPool操作Redis服务
    @Autowired
    private JedisPool jedisPool;
    //文件上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile")MultipartFile imgFile){
        String originalFilename = imgFile.getOriginalFilename();
        String extention=originalFilename.substring(originalFilename.lastIndexOf('.'));


        String fileName= UUID.randomUUID().toString()+extention;
        try {
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
    }

    @Reference
    private SetmealService setmealService;
    //新增套餐
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        try{
            setmealService.add(setmeal,checkgroupIds);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }
    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult page = setmealService.findPage(queryPageBean);
        return page;
    }
}
