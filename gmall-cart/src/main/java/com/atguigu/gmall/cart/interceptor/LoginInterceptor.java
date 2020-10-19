package com.atguigu.gmall.cart.interceptor;


import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * 拦截器。拦截本服务的请求，统一获取用户的登录状态,传递给后续的增删改方法
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor implements HandlerInterceptor {
    //    有线程安全问题
//    public String userId;

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 前置方法，handler执行之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        request.setAttribute("userId","123456");
//        THREAD_LOCAL.set(new UserInfo(10L, UUID.randomUUID().toString()));
        // 1.获取cookie中的token及user-key
        // 无状态登录 ：userKey
        // 有状态登录：token+userKey，或者token（userId）

        UserInfo userInfo = new UserInfo();
        String userKey = CookieUtils.getCookieValue(request, this.jwtProperties.getUserKey());
        if (StringUtils.isBlank(userKey)) {
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request, response, this.jwtProperties.getUserKey(), userKey, this.jwtProperties.getExpire());
        }
        userInfo.setUserKey(userKey);
        // 判断token是否为空，如果为空，直接传递userKey即可
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        if (StringUtils.isBlank(token)) {
            THREAD_LOCAL.set(userInfo);
            return true;
        }

        // 如果token不为空，解析jwt类型的token获取userId传递给后续业务
        try {
            Map<String, Object> map = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
            userInfo.setUserId(Long.valueOf(map.get("userId").toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        THREAD_LOCAL.set(userInfo);
        //

        return true;
    }

    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }

    /**
     * 后置方法，handler执行完之后执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 视图渲染完成之后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 由于使用的tomcat线程池，请求结束，线程没有结束，只是还回线程池。所有必须手动释放ThreadLocal
        // 避免内存泄漏发生
        THREAD_LOCAL.remove();
    }
}
