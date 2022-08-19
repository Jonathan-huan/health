package com.hzh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hzh.dao.CheckGroupDao;
import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.pojo.CheckGroup;
import com.hzh.pojo.CheckItem;
import com.hzh.service.CheckGroupService;
import com.hzh.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.add(checkGroup);
        Integer checkGroupId = checkGroup.getId();
        setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }
    public void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
        if(checkitemIds != null && checkitemIds.length > 0){
            for (Integer checkitemId : checkitemIds) {
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroup_id",checkGroupId);
                map.put("checkitem_id",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //使用分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> pages = checkGroupDao.selectByCondition(queryString);
        long total = pages.getTotal();
        List<CheckGroup> result = pages.getResult();
        return new PageResult(total,result);
    }

    @Override
    public void delete(Integer id) {
        checkGroupDao.deleteAssociation(id);
        checkGroupDao.deleteById(id);
    }

    @Override
    public void edit(CheckGroup checkGroup,Integer[] checkitemIds) {
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //向中间表(t_checkgroup_checkitem)插入数据（建立检查组和检查项关联关系）
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
        //更新检查组基本信息
        checkGroupDao.edit(checkGroup);
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

}
