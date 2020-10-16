package com.atguigu.gmall.cart.controll;

import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private LoginInterceptor loginInterceptor;


    @RequestMapping("/test")
    public String testInterceptor(HttpServletRequest request){
//        System.out.println(loginInterceptor.userId);
        System.out.println(request.getAttribute("userId"));

        return "hello interceptor";
    }

}
