package com.xstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstudy.base.exception.CommonError;
import com.xstudy.base.exception.BusinessException;
import com.xstudy.content.mapper.AddCourseDtoMap;
import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.content.model.dto.AddCourseDto;
import com.xstudy.content.model.dto.CourseBaseInfoDto;
import com.xstudy.content.model.dto.QueryCourseParamsDto;
import com.xstudy.content.model.po.CourseBase;
import com.xstudy.content.model.po.CourseCategory;
import com.xstudy.content.model.po.CourseMarket;
import com.xstudy.content.repository.CourseBaseMapper;
import com.xstudy.content.repository.CourseCategoryMapper;
import com.xstudy.content.repository.CourseMarketMapper;
import com.xstudy.content.service.CourseBaseService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseBaseServiceImpl implements CourseBaseService {

  private final CourseBaseMapper courseBaseMapper;

  private final CourseMarketMapper courseMarketMapper;

  private final CourseCategoryMapper courseCategoryMapper;

  private final AddCourseDtoMap addCourseDtoMap;

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

  @Transactional
  @Override
  public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
    String charge = addCourseDto.getCharge();
    if (charge.equals("201001") && addCourseDto.getPrice() == null) {
      BusinessException.cast("付费课程没有设置费用");
    }
    CourseBase newCourseBase = addCourseDtoMap.toCourseBase(addCourseDto);
    newCourseBase.setCompanyId(companyId);
    newCourseBase.setCreateDate(LocalDateTime.now());
    newCourseBase.setAuditStatus("202002");
    newCourseBase.setStatus("203001");
    int courseBaseInserted = courseBaseMapper.insert(newCourseBase);
    CourseMarket newCourseMarket = addCourseDtoMap.toCourseMarket(addCourseDto);
    newCourseMarket.setId(newCourseBase.getId());
    int courseMarketInserted = courseMarketMapper.insert(newCourseMarket);
    if (courseMarketInserted <= 0 || courseBaseInserted <= 0) {
      BusinessException.cast(CommonError.UNKNOWN_ERROR);
    }
    return getCourseBaseInfo(newCourseBase.getId());
  }

  public CourseBaseInfoDto getCourseBaseInfo(long courseId) {

    CourseBase courseBase = courseBaseMapper.selectById(courseId);
    CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
    if (courseBase == null) {
      return null;
    }
    CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
    BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
    if (courseMarket != null) {
      BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
    }
    CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
    courseBaseInfoDto.setStName(courseCategoryBySt.getName());
    CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
    courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

    return courseBaseInfoDto;
  }
}
