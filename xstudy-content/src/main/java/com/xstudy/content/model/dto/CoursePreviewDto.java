package com.xstudy.content.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class CoursePreviewDto {

  private CourseBaseInfoDto courseBase;

  private List<TeachplanDto> teachPlans;


}
