package com.xstudy.content.api;

import com.xstudy.content.model.PageParams;
import com.xstudy.content.model.PageResult;
import com.xstudy.content.model.dto.AddCourseDto;
import com.xstudy.content.model.dto.CourseBaseInfoDto;
import com.xstudy.content.model.dto.QueryCourseParamsDto;
import com.xstudy.content.model.po.CourseBase;
import com.xstudy.content.service.CourseBaseService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/course")
public class CourseBaseController {

  private final CourseBaseService courseBaseService;

  @PostMapping("list")
  public PageResult<CourseBase> list(
      PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams) {
    return courseBaseService.queryCourseBaseList(pageParams, queryCourseParams);
  }

  @PostMapping
  public CourseBaseInfoDto createCourseBase(@RequestBody @Valid AddCourseDto addCourseDto){
    return courseBaseService.createCourseBase(22L, addCourseDto);
  }
}
