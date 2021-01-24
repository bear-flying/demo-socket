package cn.itcast.haoke.dubbo.api.interceptor;

import cn.itcast.haoke.dubbo.api.controller.GraphQLController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;

/**
 * 实现 拦截Controller响应结果写入到Redis缓存
 *
 * Controller返回的都是一些json数据 在拦截器中 只拿到response和modelandview对象
 * 而这两个对象中都不能拿到响应的数据 所以显然是不适合的
 *
 * ResponseBodyAdvice是Spring提供的高级用法，会在结果被处理前拦截，
 * 我们只需要编写拦截的逻辑 就可以实现把响应数据写入Redis缓存
 *
 */
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ObjectMapper mapper = new ObjectMapper();


    /**
     * 返回true即拦截 并调用beforeBodyWrite()方法
     *
     * 这里拦截所有get请求 和 GraphQL的post请求
     *
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {

        if (returnType.hasMethodAnnotation(GetMapping.class)) {
            return true;
        }

        if (returnType.hasMethodAnnotation(PostMapping.class) &&
                StringUtils.equals(GraphQLController.class.getName(), returnType.getExecutable().getDeclaringClass().getName())) {
            return true;
        }

        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 添加缓存的逻辑 做一个try-catch的包裹 不让缓存的逻辑影响到正常的业务逻辑
        // 并且这个方法依然要把body返回 body没有做任何的修改
        try {
            String redisKey = RedisCacheInterceptor.createRedisKey(((ServletServerHttpRequest) request).getServletRequest());
            String redisValue;
            if (body instanceof String) {
                redisValue = (String) body;
            } else {
                redisValue = mapper.writeValueAsString(body);
            }
            this.redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofHours(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }
}
