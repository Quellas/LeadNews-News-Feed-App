package com.itheima;

import com.itheima.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/14 10:17
 * @description 标题
 * @package com.itheima
 */
@SpringBootApplication
public class KafkaClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaClientApplication.class, args);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        //配置手动提交offset
        factory.getContainerProperties().setAckMode((ContainerProperties.AckMode.MANUAL));
        return factory;
    }


    @RestController
    public class TestController {

        @Autowired
        private Producer producer;

        @GetMapping("/send1")
        public String sendMssage() {
            //发送消息 生产者
            producer.sendMessage();
            return "ok";
        }

        @GetMapping("/send2")
        public String sendMssage2() {
            //发送消息 生产者
            try {
                producer.sendStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "ok";
        }

    }
}
