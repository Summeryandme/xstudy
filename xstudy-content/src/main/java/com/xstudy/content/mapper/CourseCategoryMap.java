package com.xstudy.content.mapper;

import com.xstudy.content.model.dto.CourseCategoryTreeDto;
import com.xstudy.content.model.po.CourseCategory;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CourseCategoryMap {
  CourseCategoryTreeDto toCourseCategoryTreeDto(CourseCategory courseCategory);
}
