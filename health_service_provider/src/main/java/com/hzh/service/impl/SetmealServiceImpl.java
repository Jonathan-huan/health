package com.hzh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hzh.constant.RedisConstant;
import com.hzh.dao.SetmealDao;
import com.hzh.entity.PageResult;
import com.hzh.entity.QueryPageBean;
import com.hzh.pojo.CheckGroup;
import com.hzh.pojo.Setmeal;
import com.hzh.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")
    private String outputPath;
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId=setmeal.getId();
        this.setSetmealAndCheckgroup(setmealId,checkgroupIds);
        //将图片名称保存到Redis集合中
        String fileName=setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,fileName);

        //当添加套餐后需要重新生成静态页面（套餐列表页面，套餐详情页面）
        generateMobileStaticHtml();
    }
    //生成当前方法所需的静态页面
    public void generateMobileStaticHtml(){
        //在生成静态页面之前需要查询数据
        List<Setmeal> list=setmealDao.findAll();

        //需要生成套餐列表静态页面
        generateMobileSetmealListHtml(list);

        //需要生成套餐详情静态页面
        generateMobileSetmealDetailHtml(list);

    }

    private void generateMobileSetmealDetailHtml(List<Setmeal> list) {
        Map map=new HashMap();
        map.put("setmealList",list);
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }

    private void generateMobileSetmealListHtml(List<Setmeal> list) {
        for (Setmeal setmeal : list) {
            Map map=new HashMap();
            map.put("setmeal",setmealDao.findById(setmeal.getId()));
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal.getId()+".html",map);
        }
    }

    //用于生成静态页面
    public void generateHtml(String templateName,String htmlPageName,Map map){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer out=null;
        try {
            Template template = configuration.getTemplate(templateName);
            out = new FileWriter(new File(outputPath + "/" + htmlPageName));
            template.process(map,out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //使用分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> pages = setmealDao.selectByCondition(queryString);
        long total = pages.getTotal();
        List<Setmeal> result = pages.getResult();
        return new PageResult(total,result);
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    public void setSetmealAndCheckgroup(Integer setmealId,Integer[] checkgroupIds){
        if(checkgroupIds!=null&&checkgroupIds.length>0){
            for(Integer checkgroupId:checkgroupIds){
                Map<String,Integer> map=new HashMap<>();
                map.put("setmealId",setmealId);
                map.put("checkgroupId",checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }
}
