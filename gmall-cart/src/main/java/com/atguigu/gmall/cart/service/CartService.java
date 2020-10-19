package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartAsyncService asyncService;

    private static final String KEY_PREFIX = "cart:info:";
    private static final String PRICE_PREFIX = "cart:price:";

    //  购物车模型  Map<string,List<string>>
    public void addCart(Cart cart) {
        // 获取用户登录信息
        String userId = this.getUserId();
        // 通过userId或者userKey获取该用户的购物车，这个hashOps相当于内层的map一样
        String key = KEY_PREFIX + userId;
        // 通过userId或者userKey获取该用户的购物车,hashOps相当于内层map，即一个购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // todo 如果购物车不存在，则创建一个购物车

        // 判断内存的map中是否包含该商品的skuid
        BigDecimal count = cart.getCount();
        String skuId = cart.getSkuId().toString();
        if (hashOps.hasKey(skuId)) {
            // 包含则更新数量
            String cartJson = hashOps.get(skuId).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(cart.getCount().add(count));

            // 更新到mysql
            this.asyncService.updateCart(userId, cart);

        } else {
            // 不包含则给内层的map增加一条数据
            // 查询sku

            SkuEntity skuEntity = pmsClient.querySkuById(cart.getSkuId()).getData();
            if (skuEntity != null) {
                cart.setTitle(skuEntity.getTitle());
                cart.setPrice(skuEntity.getPrice());
                cart.setDefaultImage(skuEntity.getDefaultImage());
            }
            // 查询库存
            List<WareSkuEntity> wareSkuEntities = wmsClient.queryWareBySkuId(cart.getSkuId()).getData();
            if (wareSkuEntities != null) {
                boolean store = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0);
                cart.setStore(store);
            }
            // 查询营销信息
            List<ItemSaleVo> itemSaleVos = smsClient.queryItemSalesBySkuId(cart.getSkuId()).getData();
            cart.setSales(JSON.toJSONString(itemSaleVos));
            // 查询销售属性
            List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.queryAttrValueEntityBySkuId(cart.getSkuId()).getData();
            cart.setSales(JSON.toJSONString(skuAttrValueEntities));

            cart.setUserId(userId);
            cart.setCheck(true);
            // 新增到mysql
            this.asyncService.insertCart(userId.toString(),cart);
            if (skuEntity != null) {
                this.redisTemplate.opsForValue().set(PRICE_PREFIX + skuId, skuEntity.getPrice().toString());
            }
        }
        // 更新到Redis
        hashOps.put(skuId, JSON.toJSONString(cart));
    }

    /**
     * @return 可能是游客userKey，也可能是登录用户userId
     */
    private String getUserId() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        // 如果userId存在，则使用userId；如果userId为空，就是用userKey作为userId
        Long userId = userInfo.getUserId();
        if (userId == null) {
            return userInfo.getUserKey();
        }
        return userId.toString();
    }


    /**
     * 插入购物车成功的信息回调
     *
     * @param skuId
     * @return
     */
    public Cart queryCartBySkuId(Long skuId) {
        // 用户的登录信息
        String userId = this.getUserId();
        // 内层操作的map
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // 判断该用户购物车中是否包含该商品
        if (hashOps.hasKey(skuId.toString())) {
            String cartJson = hashOps.get(skuId.toString()).toString();
            return JSON.parseObject(cartJson, Cart.class);
        }
        // 制造异常
        throw new RuntimeException("该用户购物车中没有该商品！");
        //
    }

    public List<Cart> queryCarts() {
        // 获取userKey，查询为登录的购物车
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        String unloginKey = KEY_PREFIX + userKey;
        // 获取userId，查询用户登录状态，如果未登录，直接返回未登录的购物车
        BoundHashOperations<String, Object, Object> unloginHashops = this.redisTemplate.boundHashOps(unloginKey);
        List<Object> cartJsons = unloginHashops.values();
        List<Cart> unloginCarts = null;
        if (!CollectionUtils.isEmpty(cartJsons)) {
            unloginCarts = cartJsons.stream().map(cartJson -> {
                        Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                        cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId())));
                        return cart;
                    }
            ).collect(Collectors.toList());
        }
        Long userId = userInfo.getUserId();
        if (userId == null) {
            return unloginCarts;
        }
        // 如果登录，判断有咩有未登录购物车，有则合并
        String loginKey = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> loginHashops = this.redisTemplate.boundHashOps(loginKey);
        if (!CollectionUtils.isEmpty(unloginCarts)) {
            unloginCarts.forEach(cart -> {
                // 判断登录购物车中是否有该商品
                if (loginHashops.hasKey(cart.getSkuId().toString())) {
                    // 有则更新数量
                    BigDecimal addCount = cart.getCount();
                    String cartJson = loginHashops.get(cart.getSkuId().toString()).toString();
                    cart = JSON.parseObject(cartJson, Cart.class);
                    cart.setCount(cart.getCount().add(addCount));
                    // 写回Redis和mysql
                    this.asyncService.updateCart(userId.toString(), cart);

                } else {
                    // 没有则新增购物车记录
                    cart.setUserId(userId.toString());
                    this.asyncService.insertCart(userId.toString(),cart);
                }
                // 写回Redis中
                loginHashops.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
            });

        }
        // 删除未登录的购物车
        this.redisTemplate.delete(unloginKey);
        this.asyncService.deleteCartByUserIdOrUserKey(userId.toString());
        // 以userId获取登录状态的购物车
        BoundHashOperations<String, Object, Object> newLoginHashops = this.redisTemplate.boundHashOps(loginKey);
        List<Object> loginCartJsons = newLoginHashops.values();
        if (!CollectionUtils.isEmpty(loginCartJsons)) {
            return loginCartJsons.stream().map(cartJson -> {
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId())));
                return cart;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 修改购物车商品数量
     *
     * @param cart
     */
    public void updateNum(Cart cart) {
        // 获取用户登录信息
        String userId = this.getUserId();
        String key = KEY_PREFIX + userId;

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // 判断购物车中是否有该商品，有就更新数量；
        if (hashOps.hasKey(cart.getSkuId().toString())) {
            BigDecimal count = cart.getCount();
            String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(cart.getCount().add(count));
            // 写回redis
            hashOps.put(cart.getSkuId(), JSON.toJSONString(cart));
            // 写回mysql
            this.asyncService.updateCart(userId, cart);
            //
        }
    }

    /**
     * 删除购物车
     *
     * @param skuId
     */
    public void deleteCartBySkuId(Long skuId) {
        String userId = this.getUserId();
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        if (hashOps.hasKey(skuId.toString())) {
            hashOps.delete(skuId.toString());

            this.asyncService.deleteCartByUserIdAndSkuId(userId,skuId);


        }

    }
}
