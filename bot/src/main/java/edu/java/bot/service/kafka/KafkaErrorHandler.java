package edu.java.bot.service.kafka;

import edu.java.bot.service.kafka.dlq.DeadLetterQueueProducer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kafka", name = "enable")
public class KafkaErrorHandler implements CommonErrorHandler {
    private final DeadLetterQueueProducer deadLetterQueueProducer;

    @Override
    public boolean handleOne(
        Exception exception,
        ConsumerRecord<?, ?> consumerRecord,
        Consumer<?, ?> consumer,
        MessageListenerContainer container
    ) {
        handle(exception);
        return true;
    }

    @Override
    public void handleOtherException(
        Exception exception,
        Consumer<?, ?> consumer,
        MessageListenerContainer container,
        boolean batchListener
    ) {
        handle(exception);
    }

    private void handle(Exception exception) {
        deadLetterQueueProducer.send(exception.getMessage());
    }
}
