package com.hzh.service;

import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.pojo.CheckItem;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CheckItemService {
    public void add(CheckItem checkItem);
    public PageResult findPage(QueryPageBean queryPageBean);
    public void delete(Integer id);
    public void edit(CheckItem checkItem);
    public CheckItem findById(Integer id);
    public List<CheckItem> findAll();
}
