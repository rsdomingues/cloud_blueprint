package com.test.fooapp;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class ApplicationConfiguration {

    public static void main(String[] args) {
        run(ApplicationConfiguration.class, args);
    }

}
