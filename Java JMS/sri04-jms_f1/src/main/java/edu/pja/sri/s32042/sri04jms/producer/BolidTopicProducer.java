package edu.pja.sri.s32042.sri04jms.producer;

import edu.pja.sri.s32042.sri04jms.config.JmsConfig;
import edu.pja.sri.s32042.sri04jms.model.BolidInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BolidTopicProducer {
    private final JmsTemplate jmsTemplate;
    private final static Logger LOG = LoggerFactory.getLogger(BolidTopicProducer.class);

    @Scheduled(fixedRate = 10000)
    public void sendBolidInfo() {
        BolidInfo message = BolidInfo.builder()
                .messageId(BolidInfo.nextId())
                .createdAt(LocalDateTime.now())
                .message("Sending current bolid info: ")
                .engineTemp(BolidInfo.changeEngineTemp())
                .tirePressure(BolidInfo.changeTirePress())
                .oilPressure(BolidInfo.changeOilPress())
//                .messageType(BolidInfo.MessageType.OK)
                .build();
        jmsTemplate.convertAndSend(JmsConfig.TOPIC_BOLID_INFO, message);
        LOG.info("Sent message: " + message);
    }
}
