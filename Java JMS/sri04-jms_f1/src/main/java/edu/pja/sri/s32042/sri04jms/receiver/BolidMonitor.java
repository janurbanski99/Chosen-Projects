package edu.pja.sri.s32042.sri04jms.receiver;

import edu.pja.sri.s32042.sri04jms.config.JmsConfig;
import edu.pja.sri.s32042.sri04jms.model.BolidInfo;
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

@RequiredArgsConstructor
@Component
public class BolidMonitor {

    private final JmsTemplate jmsTemplate;
    private final MechanicTeam mechanicTeam;
    private final static Logger LOG = LoggerFactory.getLogger(BolidMonitor.class);

//    private final MechanicTeam mechanicTeam;
//    public BolidMonitor(MechanicTeam mechanicTeam) {
//        this.mechanicTeam = mechanicTeam;
//    }

    @JmsListener(destination = JmsConfig.TOPIC_BOLID_INFO, containerFactory = "topicConnectionFactory")
    public void receiveBolidInfo(@Payload BolidInfo convertedMessage, @Headers MessageHeaders messageHeaders, Message message) {
        int engineTemp = convertedMessage.getEngineTemp();
        double tirePress = convertedMessage.getTirePressure();
        double oilPress = convertedMessage.getOilPressure();

        //powiadamiamy tylko mechaników
        if (engineTemp > 103 && engineTemp <= 106) {
//            convertedMessage.setMessageType(BolidInfo.MessageType.MONITOR);
            convertedMessage.setMessage("HIGH ENGINE TEMPERATURE");
            jmsTemplate.convertAndSend(JmsConfig.QUEUE_BOLID_INFO, convertedMessage); //wysyła pod wskazany adres (1 parametr) przekazany komunikat (2 parametr)
            LOG.info("Sent message:" + convertedMessage); //pokazanie logów w konsoli
        } //powiadamiamy mechaników i kierowcę
        else if (engineTemp > 106) {
//            convertedMessage.setMessageType(BolidInfo.MessageType.MONITOR);
            convertedMessage.setMessage("VERY HIGH ENGINE TEMPERATURE");
            jmsTemplate.convertAndSend(JmsConfig.TOPIC_BOLID_INFO_DAM, convertedMessage);
            LOG.info("Sent message:" + convertedMessage);
        }

        if (tirePress > 4.5 && tirePress <=7.5) {    //zrobić jeszcze dla za małego
            convertedMessage.setMessage("HIGH TIRE PRESSURE");
            jmsTemplate.convertAndSend(JmsConfig.QUEUE_BOLID_INFO, convertedMessage);
            LOG.info("Sent message:" + convertedMessage);
        }
        else if (tirePress > 7.5) {
            convertedMessage.setMessage("VERY HIGH TIRE PRESSURE");
            jmsTemplate.convertAndSend(JmsConfig.TOPIC_BOLID_INFO_DAM, convertedMessage);
            LOG.info("Sent message:" + convertedMessage);
        }

        if (oilPress > 5.7) {
            convertedMessage.setMessage("HIGH OIL PRESSURE");
            jmsTemplate.convertAndSend(JmsConfig.QUEUE_BOLID_INFO, convertedMessage);
            LOG.info("Sent message:" + convertedMessage);
        } else if (oilPress < 3.2) {
            convertedMessage.setMessage("LOW OIL PRESSURE");
            jmsTemplate.convertAndSend(JmsConfig.QUEUE_BOLID_INFO, convertedMessage);
            LOG.info("Sent message:" + convertedMessage);
        }
    }
}
