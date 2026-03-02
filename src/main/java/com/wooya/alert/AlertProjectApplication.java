package com.wooya.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.wooya")
public class AlertProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertProjectApplication.class, args);
    }

}
