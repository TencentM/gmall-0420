package com.atguigu.gmall.auth.contronller;

import com.atguigu.gmall.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthContronller {

    @Autowired
    private AuthService authService;

    @GetMapping("toLogin.html")
    public String toLogin(@RequestParam(value = "returnUrl",required = false)String returnUrl, Model model){
        System.out.println("到达登录页面");
        model.addAttribute("returnUrl",returnUrl);
        System.out.println("发送视图");
        return "login";
    }

    @PostMapping("login")
    public String login(@RequestParam("loginName")String loginName, @RequestParam("password")String password,
                        @RequestParam(value = "returnUrl",required = false)String returnUrl,
                        HttpServletRequest request, HttpServletResponse response){

        System.out.println("发送登录请求！！");
        this.authService.login(loginName,password,request,response);
        System.out.println("重定向到:" + returnUrl);
//        return "redirect:http:gmall";
        return "redirect:" + returnUrl;
    }

}
