package edu.pja.sri.s32042.sri04jms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration //klasa może zawierać definicje beanów
@EnableScheduling //włączenie obsługi harmonogramowania zadań w springu
@EnableAsync //obsługa asynchroniczności w springu
public class SchedulerConfig {
    @Bean
    TaskExecutor taskExecutor() { return new SimpleAsyncTaskExecutor();
    }
}
