package com.xstudy.content;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication(scanBasePackages = "com.xstudy")
public class XstudyContentApplication {

  public static void main(String[] args) {
    SpringApplication.run(XstudyContentApplication.class, args);
  }
}
