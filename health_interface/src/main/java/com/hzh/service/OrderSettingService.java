package com.hzh.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.hzh.pojo.OrderSetting;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface OrderSettingService {
    public void add(List<OrderSetting> list);
    public List<Map> getOrderSettingByMonth(String date);
    public void editNumberByDate(OrderSetting orderSetting);
}
