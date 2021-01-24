package cn.itcast.haoke.dubbo.api.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("wx")
@RestController
public class WeiXinController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("login")
    public Map<String, Object> wxLogin(@RequestParam("code") String code) {

        Map<String, Object> result = new HashMap<>();
        result.put("status", 200);

        String appId = "wx4f50f6f7bfac644d";
        String secret = "274075546f34feeb428c325224a52836";
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId
                + "&secret=" + secret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        String jsonData = this.restTemplate.getForObject(url, String.class);
        if (StringUtils.contains(jsonData, "errcode")) {
            // 校验出错了
            result.put("status", 500);
            result.put("msg", "登录失败");
            return result;
        }

        String md5Key =  DigestUtils.md5Hex(jsonData + "HAOKE_WX_LOGIN");

        // 规则: WX_LOGIN_{MD5值}
        String redisKey = "WX_LOGIN_" + md5Key;
        this.redisTemplate.opsForValue().set(redisKey,jsonData, Duration.ofDays(7));

        result.put("ticket", "HAOKE_" + md5Key);

        return result;

    }

}
