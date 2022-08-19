package com.hzh.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hzh.constant.MessageConstant;
import com.hzh.entity.Result;
import com.hzh.pojo.OrderSetting;
import com.hzh.service.OrderSettingService;
import com.hzh.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;
    //文件上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile")MultipartFile excelFile){
        try{
            List<String[]> excel = POIUtils.readExcel(excelFile);
            List<OrderSetting> data=new ArrayList<>();
            for (String[] strings : excel) {
                String orderDate=strings[0];
                String number=strings[1];
                OrderSetting orderSetting=new OrderSetting(new Date(orderDate),Integer.parseInt(number));
                data.add(orderSetting);
            }
            orderSettingService.add(data);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }
        return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
    }

    //根据月份查询对应的预约设置数据
    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){//date格式为yyyy-MM
        try{
            List<Map> list=orderSettingService.getOrderSettingByMonth(date);
            return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_ORDERSETTING_FAIL);
        }

    }

    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try{
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }
    }
}
