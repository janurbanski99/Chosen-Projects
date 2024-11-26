package edu.pja.sri.s32042.sri04jms.receiver;

import edu.pja.sri.s32042.sri04jms.config.JmsConfig;
import edu.pja.sri.s32042.sri04jms.model.PitStopRequest;
import edu.pja.sri.s32042.sri04jms.model.PitStopResponse;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class Supervisor {
    private final JmsTemplate jmsTemplate;
    private final static Logger LOG = LoggerFactory.getLogger(Supervisor.class);

    @JmsListener(destination = JmsConfig.QUEUE_SEND_AND_RECEIVE)
    public void receiveAndRespond(@Payload PitStopRequest convertedMessage,
                                  @Headers MessageHeaders headers,
                                  Message message) throws JMSException {
        LOG.info("Received a message: " + convertedMessage);


        int num = new Random().nextInt(2);

        String response;
        if (num == 0) {
            response = "OK";
        } else {
            response = "NOT OK";
        }

        PitStopResponse msg = PitStopResponse.builder()
                .id(PitStopResponse.nextId())
                .correlatedMessageId(convertedMessage.getId())
                .createdAt(LocalDateTime.now())
                .message(response)
                .build();
        Destination replyTo = message.getJMSReplyTo();
        jmsTemplate.convertAndSend(replyTo, msg);
    }
}
