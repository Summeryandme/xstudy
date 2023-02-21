package com.xstudy.content.service;

import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.content.model.dto.AddCourseDto;
import com.xstudy.content.model.dto.CourseBaseInfoDto;
import com.xstudy.content.model.dto.EditCourseDto;
import com.xstudy.content.model.dto.QueryCourseParamsDto;
import com.xstudy.content.model.po.CourseBase;

public interface CourseBaseService {
  PageResult<CourseBase> queryCourseBaseList(
      PageParams params, QueryCourseParamsDto queryCourseParamsDto);

  CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

  CourseBaseInfoDto getCourseBaseInfo(long courseId);

  CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);
}
