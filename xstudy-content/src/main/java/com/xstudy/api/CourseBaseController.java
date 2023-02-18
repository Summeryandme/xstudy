package com.xstudy.api;

import com.xstudy.model.PageParams;
import com.xstudy.model.PageResult;
import com.xstudy.model.dto.QueryCourseParamsDto;
import com.xstudy.model.po.CourseBase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
public class CourseBaseController {
  @GetMapping("list")
  public PageResult<CourseBase> list(
      PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams) {

    return null;
  }
}
