package ru.yandex.practicum.telemetry.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.telemetry.aggregator.service.AggregationStarter;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableDiscoveryClient
public class Aggregator {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Aggregator.class, args);

        AggregationStarter aggregator = context.getBean(AggregationStarter.class);

        aggregator.start();

        context.close();
    }
}