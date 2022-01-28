package com.itheima.config;

/**
 * 流数据的监听消费者实现的接口类，系统自动会通过
 * KafkaStreamListenerFactory类扫描项目中实现该接口的类
 * 并注册为流数据的消费端
 * <p>
 * 其中泛型可是KStream或KTable
 *
 * @param <T>
 */
public interface KafkaStreamListener<T> {

    // 监听的类型  input
    String listenerTopic();

    // 处理结果发送的类  output
    String sendTopic();

    // 对象处理逻辑  处理业务的（接收 消息 进行统计 返回对象：TStream）
    T getService(T stream);

}
