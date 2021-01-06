package com.joen.apidemo;

import com.joen.apidemo.utils.Scanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiDemoApplication {

    public static void main(String[] args) {
        Scanner.init(ApiDemoApplication.class);
        SpringApplication.run(ApiDemoApplication.class, args);
    }

}
