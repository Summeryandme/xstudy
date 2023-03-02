package com.xstudy.content.service.impl;

import com.xstudy.content.model.dto.CourseBaseInfoDto;
import com.xstudy.content.model.dto.CoursePreviewDto;
import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.service.CourseBaseService;
import com.xstudy.content.service.CoursePublishService;
import com.xstudy.content.service.TeachPlanService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursePublishServiceImpl implements CoursePublishService {

  private final CourseBaseService courseBaseInfoService;

  private final TeachPlanService teachplanService;

  @Override
  public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

    CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

    List<TeachplanDto> teachPlanTree = teachplanService.getTree(courseId);

    CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
    coursePreviewDto.setCourseBase(courseBaseInfo);
    coursePreviewDto.setTeachPlans(teachPlanTree);
    return coursePreviewDto;
  }
}
