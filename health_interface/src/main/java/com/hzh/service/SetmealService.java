package com.hzh.service;

import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealService {
    public void add(Setmeal setmeal,Integer[] checkgroupIds);

    public PageResult findPage(QueryPageBean queryPageBean);

    public List<Setmeal> findAll();

    public Setmeal findById(Integer id);

    List<Map<String, Object>> findSetmealCount();
}
