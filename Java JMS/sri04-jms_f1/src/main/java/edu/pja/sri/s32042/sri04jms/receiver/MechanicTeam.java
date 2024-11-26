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

import java.util.Objects;

@Component
public class MechanicTeam {
    private final static Logger LOG = LoggerFactory.getLogger(MechanicTeam.class);

    @JmsListener(destination = JmsConfig.QUEUE_BOLID_INFO, containerFactory = "queueConnectionFactory")
    public void warn (@Payload BolidInfo convertedMessage) {
        int engineTemp = convertedMessage.getEngineTemp();
        double tirePress = convertedMessage.getTirePressure();
        double oilPress = convertedMessage.getOilPressure();
//        BolidInfo.MessageType messageType = convertedMessage.getMessageType();

        LOG.info("Mechanic Team received message: {}", convertedMessage);

        if ("HIGH ENGINE TEMPERATURE".equals(convertedMessage.getMessage())) {   //musi być equals() bo == porównuje referencje do obiektów a nie ich zawartość
            LOG.warn("Engine temperature too high! Current temp: {}", engineTemp);
        }
        if ("HIGH TIRE PRESSURE".equals(convertedMessage.getMessage())) {
            LOG.warn("Tire pressure too high! Current pressure: {}", tirePress);
        }
        if ("HIGH OIL PRESSURE".equals(convertedMessage.getMessage())) {
            LOG.warn("Oil pressure too high! Current pressure: {}", oilPress);
        }
        if ("LOW OIL PRESSURE".equals(convertedMessage.getMessage())) {
            LOG.warn("Oil pressure too low! Current pressure: {}", oilPress);
        }
    }

    @JmsListener(destination = JmsConfig.TOPIC_BOLID_INFO_DAM, containerFactory = "topicConnectionFactory")
    public void warnExtreme (@Payload BolidInfo convertedMessage) {
        int engineTemp = convertedMessage.getEngineTemp();
        double tirePress = convertedMessage.getTirePressure();
        LOG.info("Mechanic Team received message: {}", convertedMessage);
        if ("VERY HIGH ENGINE TEMPERATURE".equals(convertedMessage.getMessage())) {
            LOG.warn("Engine temperature extremely high! Current temp: {}", engineTemp);
        }
        if ("VERY HIGH TIRE PRESSURE".equals(convertedMessage.getMessage())) {
            LOG.warn("Tire pressure extremely high! Current pressure: {}", tirePress);
        }
     }
}
