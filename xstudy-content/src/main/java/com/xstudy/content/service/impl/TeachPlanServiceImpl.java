package com.xstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xstudy.base.exception.BusinessException;
import com.xstudy.content.mapper.TeachPlanMap;
import com.xstudy.content.model.dto.SaveTeachPlanDto;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeachPlanServiceImpl implements TeachPlanService {

  private final TeachplanMapper teachplanMapper;

  private final TeachPlanMap teachPlanMap;

  private final TeachplanMediaMapper teachplanMediaMapper;

  @Override
  public List<TeachplanDto> getTree(long courseId) {
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

  @Override
  @Transactional
  public void save(SaveTeachPlanDto teachPlanDto) {
    Long id = teachPlanDto.getId();
    if (id != null) {
      Teachplan teachplan = teachplanMapper.selectById(id);
      BeanUtils.copyProperties(teachPlanDto, teachplan);
      teachplanMapper.updateById(teachplan);
    } else {
      int count = getTeachPlanCount(teachPlanDto.getCourseId(), teachPlanDto.getParentid());
      Teachplan teachPlanNew = new Teachplan();
      teachPlanNew.setOrderby(count + 1);
      BeanUtils.copyProperties(teachPlanDto, teachPlanNew);
      teachplanMapper.insert(teachPlanNew);
    }
  }

  @Override
  public void delete(Long teachPlanId) {
    LambdaQueryWrapper<Teachplan> queryWrapper = Wrappers.lambdaQuery(Teachplan.class);
    queryWrapper.eq(Teachplan::getParentid, teachPlanId);
    List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper);
    if (!teachplanList.isEmpty()) {
      throw BusinessException.info("课程计划信息还有子级信息，无法操作");
    }
    teachplanMapper.deleteById(teachPlanId);
  }

  private int getTeachPlanCount(Long courseId, Long parentId) {
    LambdaQueryWrapper<Teachplan> queryWrapper = Wrappers.lambdaQuery(Teachplan.class);
    queryWrapper.eq(Teachplan::getCourseId, courseId);
    queryWrapper.eq(Teachplan::getParentid, parentId);
    return teachplanMapper.selectCount(queryWrapper);
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
