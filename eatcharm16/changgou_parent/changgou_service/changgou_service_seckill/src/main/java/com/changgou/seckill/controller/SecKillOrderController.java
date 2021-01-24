package com.changgou.seckill.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.config.TokenDecode;
import com.changgou.seckill.service.SecKillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillorder")
public class SecKillOrderController {

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    private SecKillOrderService secKillOrderService;

    /**
     * 异步下单
     *
     * 业务逻辑：
     * 获取秒杀商品数据与库存量数据，如果没有库存则抛出异常
     * 执行redis预扣减库存,并获取扣减之后的库存值
     * 如果扣减完的库存值<=0, 则删除redis中对应的商品信息与库存信息
     * 基于mq异步方式完成与mysql数据同步（异步下单微服务 实现最终一致性）
     */
    @RequestMapping("/add")
    public Result add(@RequestParam("time") String time, @RequestParam("id") Long id){
        //1.动态获取到当前的登录人
        String username = tokenDecode.getUserInfo().get("username");

        //2.基于业务层进行秒杀下单
        boolean result = secKillOrderService.add(id, time, username);

        //3.返回结果
        if (result){
            //下单成功
            return new Result(true, StatusCode.OK,"下单成功");
        }else{
            //下单失败
            return new Result(false,StatusCode.ERROR,"下单失败");
        }
    }
}
