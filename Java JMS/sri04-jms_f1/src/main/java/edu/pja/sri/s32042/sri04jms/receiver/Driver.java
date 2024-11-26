package edu.pja.sri.s32042.sri04jms.receiver;

import edu.pja.sri.s32042.sri04jms.config.JmsConfig;
import edu.pja.sri.s32042.sri04jms.model.BolidInfo;
import edu.pja.sri.s32042.sri04jms.model.PitStopRequest;
import edu.pja.sri.s32042.sri04jms.model.PitStopResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Driver {
    private final static Logger LOG = LoggerFactory.getLogger(Driver.class);

    @JmsListener(destination = JmsConfig.TOPIC_BOLID_INFO_DAM, containerFactory = "topicConnectionFactory")
    public void warnExtreme (@Payload BolidInfo convertedMessage) {
        LOG.info("Driver received message: {}", convertedMessage);

        if ("VERY HIGH ENGINE TEMPERATURE".equals(convertedMessage.getMessage())) {
            LOG.info("Stopping the vehicle due to " + convertedMessage.getMessage());
            System.exit(0);
        }
        if ("VERY HIGH TIRE PRESSURE".equals(convertedMessage.getMessage())) {
            LOG.info("Stopping the vehicle due to " + convertedMessage.getMessage());
            System.exit(0);
        }
     }
}

@Component
@RequiredArgsConstructor
class PitStopRequestReplyProducer {
    private final JmsTemplate jmsTemplate;
    private final JmsMessagingTemplate jmsMessagingTemplate;

    private final static Logger LOG = LoggerFactory.getLogger(PitStopRequestReplyProducer.class);
    private boolean firstRun = true;

    @Scheduled(fixedRate = 30005)
    public void sendAndReceive() {

        if (firstRun) {
            firstRun = false;
            return;
        }

        PitStopRequest message = PitStopRequest.builder()
                .id(PitStopRequest.nextId())
                .createdAt(LocalDateTime.now())
                .message("I need to make a pit stop")
                .build();

        jmsMessagingTemplate.setJmsTemplate(jmsTemplate);
        LOG.info("I'm sending a request: " + message);
        PitStopResponse responseMsg = jmsMessagingTemplate.convertSendAndReceive(
                JmsConfig.QUEUE_SEND_AND_RECEIVE,
                message,
                PitStopResponse.class);
        String responseText = responseMsg.getMessage();
        LOG.info("I've received a response: " + responseText);

        if ("OK".equals(responseText)) {
            LOG.info("Making a pitstop");
            System.exit(0);
        } else {
            LOG.info("Continuing the ride...");
        }
//        DONE dodać że zjeżdżam albo continue ride...
    }
}
