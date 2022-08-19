package com.hzh.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hzh.dao.MemberDao;
import com.hzh.pojo.Member;
import com.hzh.service.MemberService;
import com.hzh.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService{
    @Autowired
    private MemberDao memberDao;
    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        String password = member.getPassword();
        if(password!=null){
            //使用md5将明文密码进行加密
            password= MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> month) {
        List<Integer> list=new ArrayList<>();
        for (String m : month) {
            m=m+".31";
            Integer count=memberDao.findMemberCountBeforeDate(m);
            list.add(count);
        }
        return list;
    }
}
