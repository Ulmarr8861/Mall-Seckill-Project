package com.miaoshaproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan("com.miaoshaproject.mapper")
@EnableTransactionManagement
public class Application {
    public static void main( String[] args ) { SpringApplication.run(Application.class, args); }
}
