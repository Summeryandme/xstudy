package com.xstudy.content.mapper;


import com.xstudy.content.model.dto.AddCourseDto;
import com.xstudy.content.model.po.CourseBase;
import com.xstudy.content.model.po.CourseMarket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface AddCourseDtoMap {

  CourseBase toCourseBase(AddCourseDto addCourseDto);

  CourseMarket toCourseMarket(AddCourseDto addCourseDto);
}
