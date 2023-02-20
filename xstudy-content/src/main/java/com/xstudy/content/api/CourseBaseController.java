package com.xstudy.content.api;

import com.xstudy.base.exception.ValidationGroups;
import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.content.model.dto.AddCourseDto;
import com.xstudy.content.model.dto.CourseBaseInfoDto;
import com.xstudy.content.model.dto.EditCourseDto;
import com.xstudy.content.model.dto.QueryCourseParamsDto;
import com.xstudy.content.model.po.CourseBase;
import com.xstudy.content.service.CourseBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseBaseController {

  private final CourseBaseService courseBaseService;

  @PostMapping("list")
  public PageResult<CourseBase> list(
      PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams) {
    return courseBaseService.queryCourseBaseList(pageParams, queryCourseParams);
  }

  @PostMapping
  public CourseBaseInfoDto createCourseBase(
      @RequestBody @Validated(value = {ValidationGroups.Insert.class}) AddCourseDto addCourseDto) {
    return courseBaseService.createCourseBase(22L, addCourseDto);
  }

  @GetMapping("{courseId}")
  public CourseBaseInfoDto getCourseBaseById(@PathVariable(value = "courseId") Long courseId) {
    return courseBaseService.getCourseBaseInfo(courseId);
  }

  @PutMapping
  public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto) {
    return courseBaseService.updateCourseBase(22L, editCourseDto);
  }
}
