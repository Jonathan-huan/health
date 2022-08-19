package com.hzh.dao;

import com.github.pagehelper.Page;
import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.pojo.CheckItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface CheckItemDao {
    public void add(CheckItem checkItem);
    public Page<CheckItem> selectByCondition(String value);
    public long findCountByCheckItemId(Integer id);
    public void deleteById(Integer id);
    public CheckItem findById(Integer id);
    public void edit(CheckItem checkItem);
    public List<CheckItem> findAll();
}
