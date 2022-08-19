package com.hzh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hzh.dao.OrderSettingDao;
import com.hzh.pojo.OrderSetting;
import com.hzh.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    
    //批量导入预约设置数据
    @Override
    public void add(List<OrderSetting> list) {
        if(list!=null&&list.size()>0){
            for (OrderSetting orderSetting : list) {
                //判断当前日期是否已经进行了预约设置
                long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if(countByOrderDate>0){
                    //已经进行了预约设置，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else{
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        String begin=date+"-1";
        String end=date+"-31";
        Map<String,String> map=new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        //调用dao，根据日期范围查询预约设置数据
        List<OrderSetting> orderSettingByMonth = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> result=new ArrayList<>();
        if(orderSettingByMonth!=null&&orderSettingByMonth.size()>0){
            for (OrderSetting orderSetting : orderSettingByMonth) {
                Map<String,Object> m=new HashMap<>();
                m.put("date",orderSetting.getOrderDate().getDate());//获取日期数字，几号
                m.put("number",orderSetting.getNumber());
                m.put("reservations",orderSetting.getReservations());
                result.add(m);
            }
        }

        return result;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count>0){
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            orderSettingDao.add(orderSetting);
        }
    }
}
