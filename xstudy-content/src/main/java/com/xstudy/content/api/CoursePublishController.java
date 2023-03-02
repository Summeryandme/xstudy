package com.xstudy.content.api;

import com.xstudy.content.model.dto.CoursePreviewDto;
import com.xstudy.content.service.CoursePublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class CoursePublishController {

  private final CoursePublishService coursePublishService;

  @GetMapping("/coursepreview/{courseId}")
  public ModelAndView preview(@PathVariable("courseId") Long courseId) {

    // 获取课程预览信息
    CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addObject("model", coursePreviewInfo);
    modelAndView.setViewName("course_template");
    return modelAndView;
  }
}
