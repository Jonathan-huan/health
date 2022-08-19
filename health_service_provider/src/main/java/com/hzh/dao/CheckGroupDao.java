package com.hzh.dao;

import com.github.pagehelper.Page;
import com.hzh.pojo.CheckGroup;
import com.hzh.pojo.CheckItem;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    public void add(CheckGroup checkGroup);
    public void setCheckGroupAndCheckItem(Map<String,Integer> map);
    public Page<CheckGroup> selectByCondition(String value);
    public void deleteById(Integer id);
    public void deleteAssociation(Integer id);
    public CheckGroup findById(Integer id);
    public void edit(CheckGroup checkGroup);
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);
    List<CheckGroup> findAll();
}
