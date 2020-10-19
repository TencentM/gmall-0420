package com.atguigu.gmall.cart.config;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * jwt配置读取类，读取配置文件中的jwt配置信息
 */
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {

    private String pubKeyPath;
    private String cookieName;
    private String userKey;
    private Integer expire;

    private PublicKey publicKey;

    @PostConstruct
    public void init(){
        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
