package com.hzh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hzh.constant.MessageConstant;
import com.hzh.dao.MemberDao;
import com.hzh.dao.OrderDao;
import com.hzh.dao.OrderSettingDao;
import com.hzh.entity.Result;
import com.hzh.pojo.Member;
import com.hzh.pojo.Order;
import com.hzh.pojo.OrderSetting;
import com.hzh.service.OrderService;
import com.hzh.service.OrderSettingService;
import com.hzh.utils.DateUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public Result order(Map map) throws Exception {
        //1.检查用户所选择的预约日期是否已经进行了预约设置，如果没有设置则无法进行预约
        String orderDate = (String)map.get("orderDate");
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));
        if(orderSetting==null){
            //指定日期没有进行预约设置，无法完成体检预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2.检查用户所选择的预约日期是否已经预约满，如果已经预约满则无法进行预约
        int number=orderSetting.getNumber();//可预约人数
        int reservations=orderSetting.getReservations();//已预约人数
        if(reservations>=number){
            //人数已满
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //3.检查用户是否重复预约（同一个用户在同一天预约了同一个套餐，如果是重复预约则无法完成再次预约）
        String telephone = (String) map.get("telephone");
        Member member=memberDao.findByTelephone(telephone);
        if(member!=null){
            //判断是否在重复预约
            Integer  memberId=member.getId();
            Date date=DateUtils.parseString2Date(orderDate);
            String setmealId=(String) map.get("setmealId");
            Order order = new Order(memberId, date, Integer.parseInt(setmealId));
            //根据条件进行查询
            List<Order> list = orderDao.findByCondition(order);
            if(list!=null&&list.size()>0){
                //说明用户在重复预约，无法完成再次预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }else{
            //4.检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
            member=new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);//自动完成注册
        }


        //5.预约成功，更新当日的已预约人数
        Order order=new Order();
        order.setMemberId(member.getId());
        order.setOrderDate(DateUtils.parseString2Date(orderDate));
        order.setOrderType((String) map.get("orderType"));
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setSetmealId(Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);
        orderSetting.setReservations(orderSetting.getReservations()+1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if(map!=null){
            //处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }

    //体检预约

}
