package com.atguigu.gmall.auth.service;

import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private GmallUmsClient umsClient;
    @Autowired
    private JwtProperties jwtProperties;

    public void login(String loginName, String password, HttpServletRequest request, HttpServletResponse response) {
        // 调用接口查询查询用户
        UserEntity userEntity = umsClient.queryUser(loginName, password).getData();
        // 对用户信息判空
        if (userEntity == null){
            throw new RuntimeException("用户名或者密码错误！");
        }
        // 组装载荷信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userEntity.getId());
        map.put("username",userEntity.getUsername());
        // 防止被盗用，可加入当前用户IP
        String ip = IpUtil.getIpAddressAtService(request);
        map.put("ip",ip);

        try {
            // 生成jwt类型的token
            String token = JwtUtils.generateToken(map, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());

            // 吧token存入cookie中
            CookieUtils.setCookie(request,response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getExpire()*60);

            // 7.为了登录成功之后显示用户昵称
            CookieUtils.setCookie(request, response, this.jwtProperties.getNickName(), userEntity.getNickname(), this.jwtProperties.getExpire() * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
