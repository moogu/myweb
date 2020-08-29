package com.moogu.myweb.config;

import com.github.mongobee.Mongobee;
import org.springframework.context.annotation.Bean;

public class MongobeeConfig {
    @Bean
    public Mongobee mongobee() {
        Mongobee runner = new Mongobee("mongodb://localhost:27017/local?authSource=admin");
        runner.setDbName("local");
        runner.setChangeLogsScanPackage("com.moogu.myweb.model.changelogs");
        return runner;
    }
}
