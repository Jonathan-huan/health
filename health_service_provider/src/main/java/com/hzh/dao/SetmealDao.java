package com.hzh.dao;

import com.github.pagehelper.Page;
import com.hzh.pojo.CheckGroup;
import com.hzh.pojo.Setmeal;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SetmealDao {
    public void add(Setmeal setmeal);
    public void setSetmealAndCheckGroup(Map<String,Integer> map);
    public List<Setmeal> findAll();
    public Page<Setmeal> selectByCondition(String queryString);

    public Setmeal findById(Integer id);

    public List<Map<String, Object>> findSetmealCount();
}
