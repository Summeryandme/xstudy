package com.xstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xstudy.content.mapper.TeachPlanMap;
import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.model.po.Teachplan;
import com.xstudy.content.model.po.TeachplanMedia;
import com.xstudy.content.repository.TeachplanMapper;
import com.xstudy.content.repository.TeachplanMediaMapper;
import com.xstudy.content.service.TeachPlanService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeachPlanServiceImpl implements TeachPlanService {

  private final TeachplanMapper teachplanMapper;

  private final TeachPlanMap teachPlanMap;

  private final TeachplanMediaMapper teachplanMediaMapper;

  @Override
  public List<TeachplanDto> findTeachPlanTree(long courseId) {
    LambdaQueryWrapper<Teachplan> queryWrapper = Wrappers.lambdaQuery(Teachplan.class);
    queryWrapper.eq(Teachplan::getCourseId, courseId);
    List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper);
    List<TeachplanDto> teachplanDtoList =
        teachplanList.stream().map(teachPlanMap::toTeachPanDto).collect(Collectors.toList());
    List<TeachplanDto> roots =
        teachplanDtoList.stream()
            .filter(teachplanDto -> teachplanDto.getParentid() == 0)
            .collect(Collectors.toList());
    roots.forEach(
        teachplanDto -> {
          TeachplanMedia teachplanMedia =
              teachplanMediaMapper.selectOne(
                  Wrappers.lambdaQuery(TeachplanMedia.class)
                      .eq(TeachplanMedia::getTeachplanId, teachplanDto.getId()));
          teachplanDto.setTeachplanMedia(teachplanMedia);
          teachplanDto.setTeachPlanTreeNodes(getChildren(teachplanDtoList, teachplanDto));
        });
    return roots;
  }

  private List<TeachplanDto> getChildren(
      List<TeachplanDto> teachplanDtoList, TeachplanDto teachplanDto) {
    List<TeachplanDto> children =
        teachplanDtoList.stream()
            .filter(dto -> Objects.equals(dto.getParentid(), teachplanDto.getId()))
            .collect(Collectors.toList());
    children.forEach(dto -> dto.setTeachPlanTreeNodes(getChildren(teachplanDtoList, dto)));
    return children;
  }
}
