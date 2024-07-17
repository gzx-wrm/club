package com.gzx.club.circle.server.interceptor;

import com.gzx.club.circle.server.util.LoginUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @program: club
 * @description: 拦截feign发送的请求，将openId带到请求头中
 * @author: gzx
 * @create: 2024-06-29 13:58
 **/
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (Objects.nonNull(LoginUtil.getLoginId())) {
            requestTemplate.header("loginId", LoginUtil.getLoginId());
        }
    }
}
