package com.xstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstudy.content.mapper.CourseBaseMapper;
import com.xstudy.content.model.PageParams;
import com.xstudy.content.model.PageResult;
import com.xstudy.content.model.dto.QueryCourseParamsDto;
import com.xstudy.content.model.po.CourseBase;
import com.xstudy.content.service.CourseBaseService;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CourseBaseServiceImpl implements CourseBaseService {

  @Resource private CourseBaseMapper courseBaseMapper;

  @Override
  public PageResult<CourseBase> queryCourseBaseList(
      PageParams params, QueryCourseParamsDto queryCourseParamsDto) {
    LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
    String courseName = queryCourseParamsDto.getCourseName();
    queryWrapper.apply(
        StringUtils.isNotEmpty(courseName),
        "lower(name) like {0}",
        "%" + courseName.toLowerCase() + "%");
    String auditStatus = queryCourseParamsDto.getAuditStatus();
    queryWrapper.eq(StringUtils.isNotEmpty(auditStatus), CourseBase::getAuditStatus, auditStatus);
    String publishStatus = queryCourseParamsDto.getPublishStatus();
    queryWrapper.eq(StringUtils.isNotEmpty(publishStatus), CourseBase::getStatus, publishStatus);
    Page<CourseBase> courseBasePage =
        courseBaseMapper.selectPage(
            new Page<>(params.getPageNo(), params.getPageSize()), queryWrapper);
    return new PageResult<>(
        courseBasePage.getRecords(),
        courseBasePage.getTotal(),
        params.getPageNo(),
        params.getPageSize());
  }
}
