package com.xstudy.content.mapper;


import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.model.po.Teachplan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface TeachPlanMap {
  TeachplanDto toTeachPanDto(Teachplan teachplan);
}
