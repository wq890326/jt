package com.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.util.ConnectionUtil;

public class Send {

    private final static String QUEUE_NAME = "jt_test_queue";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();

        // 声明（创建）队列，如果队列不存在就创建，存在就不创建。
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 消息内容
        for(int i=0;i<100000;i++){
        	String message = "jt Hello World!"+i;
        	channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        	System.out.println(" [x] Sent '" + message + "'");
        }
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}