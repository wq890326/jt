package com.rabbitmq.ps;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.util.ConnectionUtil;

public class Send {

    private final static String EXCHANGE_NAME = "jt_test_exchange_fanout";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 声明exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // 消息内容
        for(int i=0;i<100;i++){
        	String message = "Hello World!"+i;
        	channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
        	System.out.println(" [x] Sent '" + message + "'");
        }
        
        channel.close();
        connection.close();
    }
}