package com.hzh.service;

import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.pojo.CheckGroup;
import com.hzh.pojo.CheckItem;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CheckGroupService {
    public void add(CheckGroup checkGroup,Integer[] checkitemIds);
    public PageResult findPage(QueryPageBean queryPageBean);
    public void delete(Integer id);
    public void edit(CheckGroup checkGroup,Integer[] checkitemIds);
    public CheckGroup findById(Integer id);
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);
    public List<CheckGroup> findAll();
}
