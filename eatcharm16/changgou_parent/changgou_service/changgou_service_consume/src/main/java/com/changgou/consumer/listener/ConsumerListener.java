package com.changgou.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.consumer.config.RabbitMQConfig;
import com.changgou.consumer.service.SecKillOrderService;
import com.changgou.seckill.pojo.SeckillOrder;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerListener {

    @Autowired
    private SecKillOrderService secKillOrderService;

    /**
     *
     * 基于MQ异步方式完成与MySQL数据同步（由异步下单微服务实现最终一致性)
     *
     * 除了字符串类型 还可以选用Message对象接收 在Message对象中 就包含了接收到的消息
     */
    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    public void receiveSecKillOrderMessage(Message message, Channel channel){

        //设置预抓取总数
        try {
            channel.basicQos(300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //1.转换消息格式
        SeckillOrder seckillOrder = JSON.parseObject(message.getBody(), SeckillOrder.class);

        //2.基于业务层完成同步mysql的操作
        int result = secKillOrderService.createOrder(seckillOrder);
        if (result>0){

            //同步mysql成功
            //向消息服务器返回成功通知
            try {
                /**
                 * 第一个参数:消息的唯一标识
                 * 第二个参数:是否开启批处理
                 *
                 * 调用channel的basicAck()签收
                 * 消息服务器接收到消费者成功通知的时候 就会根据消息中的唯一标识把这个消息删除
                 */
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //同步mysql失败
            //向消息服务器返回失败通知
            try {
                /**
                 * 第一个参数:消息的唯一标识
                 * 第二个参数: true所有消费者都会拒绝这个消息,false只有当前消费者拒绝
                 * 第三个参数:true当前消息会进入到死信队列(延迟消息队列),false当前的消息会重新进入到原有队列中,默认回到头部
                 *
                 * 调用channel的basicNack()拒绝签收
                 * 消息服务器接收到消费者失败通知的时候 就可以根据消息中的唯一标识 把消息放入到死信队列中或者进行重新的发送
                 */
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
