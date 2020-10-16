package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.mapper.UserMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();

        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("phone", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public void register(String code, UserEntity userEntity) {
        // TODO 1.校验验证码，查询Redis中的验证码和用户输入的验证码比较

        // 2 生成随机salt，加盐
        String salt = UUID.randomUUID().toString();
        userEntity.setSalt(salt);
        // 对密码加盐加密
        userEntity.setPassword(DigestUtils.md5Hex(userEntity.getPassword() + salt));

        // 3 新增用户信息
        userEntity.setCreateTime(new Date());
        userEntity.setLevelId(1L);
        userEntity.setGrowth(1000);
        userEntity.setIntegration(1000);
        userEntity.setSourceType(1);
        userEntity.setStatus(1);
        this.save(userEntity);
        //4 TODO 删除Redis中的短信验证码

    }

    @Override
    public UserEntity queryUser(String loginName, String password) {
        // 根据用户名查询到用户信息
        UserEntity loginUser = this.getOne(new QueryWrapper<UserEntity>()
                .eq("username", loginName)
                .or().eq("email", loginName)
                .or().eq("phone", loginName)
        );

        // 判断用户是否存在
        if (loginUser == null) {
            return null;
        }

        // 将穿过来的password加密
        String salt = loginUser.getSalt();
        password = DigestUtils.md5Hex(password + salt);
        // 比较数据库中的密码和传过来的password加密后是否相等
        if (StringUtils.equals(password, loginUser.getPassword())) {
            return loginUser;
        }
        return null;
        //
        //

    }


}