package com.xstudy.system;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableSwagger2Doc
@SpringBootApplication
public class XstudySystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(XstudySystemApplication.class, args);
  }
}
