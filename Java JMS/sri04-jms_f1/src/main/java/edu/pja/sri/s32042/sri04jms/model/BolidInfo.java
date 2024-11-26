package edu.pja.sri.s32042.sri04jms.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.apache.commons.math3.util.Precision;

import java.time.LocalDateTime;
import java.util.Random;

@Data
@Builder    //konieczne oznaczenie żeby później korzystać z buildera (jak w helloworldqueueproducer)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BolidInfo {
    private static long idIndex = 0;
    public static long nextId() { return idIndex++; }

    private static int initialEngTemp = 100;
    public static int changeEngineTemp() { return initialEngTemp++;}

    private static double initialOilPress = 4.0;    //DONE zrobić losowy zakres jakiś
    public static double changeOilPress() {
        double min = -1;
        double max = 2;
        Random random = new Random();
        double change = min + (max - min) * random.nextDouble();
        return Precision.round(initialOilPress + change, 2);
    }

    private static double initialTirePress = 3.0 - 0.5;
    public static double changeTirePress() { return initialTirePress+=0.5;}

//    public enum MessageType {
//        OK,
//        MONITOR,
//        STOP
//    }

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    private String message;
    private long messageId;
//    private MessageType messageType; //OK / MONITOR / STOP
    private int engineTemp;
    private double tirePressure;
    private double oilPressure;
}
