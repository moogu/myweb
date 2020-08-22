package com.moogu.myweb.config;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class Initializer {

    @Bean
    public CommandLineRunner startApplication() {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.info("Starting Application");
        };
    }

}