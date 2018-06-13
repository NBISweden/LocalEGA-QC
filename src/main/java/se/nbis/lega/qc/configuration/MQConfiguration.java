package se.nbis.lega.qc.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.nbis.lega.qc.listeners.QCMessageListener;

@Configuration
public class MQConfiguration {

    private String queueName;

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, QCMessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(messageListener);
        return container;
    }

    @Value("${lega.qc.mq.queue}")
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

}
