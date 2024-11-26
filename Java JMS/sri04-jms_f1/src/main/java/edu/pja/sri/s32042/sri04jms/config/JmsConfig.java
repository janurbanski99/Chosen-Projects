package edu.pja.sri.s32042.sri04jms.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

@Configuration
@EnableJms
public class JmsConfig {    //klasa w celu określenia konwersji komunikatów tekstowych na obiekty
    // .QUEUE / TOPIC - JMS odróżnia dzięki temu rodzaj komunikacji
    public static final String TOPIC_BOLID_INFO = "INFO.TOPIC";
    public static final String TOPIC_BOLID_INFO_DAM = "INFODAM.TOPIC"; //DAM - driver & mechanics
    public static final String QUEUE_BOLID_INFO = "INFO.QUEUE";
    public static final String QUEUE_SEND_AND_RECEIVE = "SEND_RECEIVE.QUEUE";

    @Bean
    public JmsListenerContainerFactory<?> queueConnectionFactory( //umozliwienie słuchaczom połączenie do kanałów komunikacji odpowiedniego typu
            //Qualifier - wskazanie, który bean ConnectionFactory powinien być wstrzyknięty
            @Qualifier("jmsConnectionFactory")ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false); //false - skonfigurowane do modelu komunikacji point-to-point
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicConnectionFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public DynamicDestinationResolver destinationResolver() { //wskazanie na typ destynacji na podstawie nazwy (kolejka czy temat)
        return new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(
                    Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                if (destinationName.endsWith(".TOPIC")) {
                    pubSubDomain = true;
                }
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            }
        };
    }

    @Bean //konwersja obiektów na tekst w formacie JSON i na odwrót
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
