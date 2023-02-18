package com.xstudy.content.service;

import com.xstudy.content.model.PageParams;
import com.xstudy.content.model.PageResult;
import com.xstudy.content.model.dto.QueryCourseParamsDto;
import com.xstudy.content.model.po.CourseBase;

public interface CourseBaseService {
  PageResult<CourseBase> queryCourseBaseList(
      PageParams params, QueryCourseParamsDto queryCourseParamsDto);
}
