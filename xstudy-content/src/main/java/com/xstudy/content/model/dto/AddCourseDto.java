package com.xstudy.content.model.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
@Data
public class AddCourseDto {

 @NotEmpty(message = "课程名称不能为空")
 private String name;

 @NotEmpty(message = "适用人群不能为空")
 private String users;

 private String tags;

 @NotEmpty(message = "课程分类不能为空")
 private String mt;

 @NotEmpty(message = "课程分类不能为空")
 private String st;

 @NotEmpty(message = "课程等级不能为空")
 private String grade;

 private String teachmode;

 private String description;

 private String pic;

 @NotEmpty(message = "收费规则不能为空")
 private String charge;

 private Float price;
 private Float originalPrice;


 private String qq;

 private String wechat;
 private String phone;

 private Integer validDays;
}
