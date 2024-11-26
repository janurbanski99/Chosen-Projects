package edu.pja.sri.s32042.sri04jms.receiver;

import edu.pja.sri.s32042.sri04jms.config.JmsConfig;
import edu.pja.sri.s32042.sri04jms.model.BolidInfo;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BolidLogger {
    private final static Logger LOG = LoggerFactory.getLogger(BolidLogger.class);

    @JmsListener(destination = JmsConfig.TOPIC_BOLID_INFO, containerFactory = "topicConnectionFactory")
    public void receiveBolidInfo(@Payload BolidInfo convertedMessage, @Headers MessageHeaders messageHeaders, Message message) {
        LOG.info("Received a message: " + convertedMessage);
    }
}
