package com.atguigu.gmall.cart.controll;

import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//@Controller
@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private LoginInterceptor loginInterceptor;

    @GetMapping()
    @ApiOperation("添加商品到购物车")
    public String addCart(Cart cart){
        this.cartService.addCart(cart);
        return "redirect:http://cart.gmall.com/addCart.html?skuId=" + cart.getSkuId();
    }

    @GetMapping("addCart.html")
    @ApiOperation("跳转到添加购物车成功页面")
    public String queryCartBySkuId(@RequestParam("skuId")Long skuId, Model model){
        Cart cart = this.cartService.queryCartBySkuId(skuId);
        model.addAttribute("cart", cart);
        return "addCart";
    }

    @GetMapping("cart.html")
    @ApiOperation("查询购物车，并跳转到购物车页面")
    public String queryCarts(Model model){
        List<Cart> cartList = this.cartService.queryCarts();
        model.addAttribute("carts", cartList);
        return "cart";
    }

    @PostMapping("updateNum")
    @ApiOperation("修改购物车中商品数量")
    @ResponseBody
    public ResponseVo updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);
        return ResponseVo.ok();
    }

    @PostMapping("deleteCart")
    @ResponseBody
    public ResponseVo deleteCartBySkuId(@RequestParam("skuId")Long skuId){
        this.cartService.deleteCartBySkuId(skuId);
        return ResponseVo.ok();
    }

    @GetMapping("test")
    public ResponseVo testInterceptor(HttpServletRequest request){
//        System.out.println(loginInterceptor.userId);
        System.out.println("----------");
//        System.out.println(request.getAttribute("userId"));
        System.out.println(LoginInterceptor.getUserInfo());

        return ResponseVo.ok();
    }

}
