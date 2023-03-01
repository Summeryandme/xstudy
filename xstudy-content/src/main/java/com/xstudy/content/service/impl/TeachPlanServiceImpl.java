package com.xstudy.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xstudy.base.exception.BusinessException;
import com.xstudy.content.mapper.TeachPlanMap;
import com.xstudy.content.model.dto.BindTeachplanMediaDto;
import com.xstudy.content.model.dto.SaveTeachPlanDto;
import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.model.po.Teachplan;
import com.xstudy.content.model.po.TeachplanMedia;
import com.xstudy.content.repository.TeachplanMapper;
import com.xstudy.content.repository.TeachplanMediaMapper;
import com.xstudy.content.service.TeachPlanService;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    LambdaQueryWrapper<TeachplanMedia> mediaLambdaQueryWrapper =
        Wrappers.lambdaQuery(TeachplanMedia.class);
    mediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
    teachplanMediaMapper.delete(mediaLambdaQueryWrapper);
  }

  @Override
  @Transactional
  public void moveDown(Long teachPlanId) {
    Teachplan upPlan =
        teachplanMapper.selectOne(
            Wrappers.lambdaQuery(Teachplan.class).eq(Teachplan::getId, teachPlanId));
    List<Teachplan> sortedPlans = getSortedSameParentPlans(upPlan);
    int index = sortedPlans.indexOf(upPlan);
    if (index + 1 >= sortedPlans.size()) {
      throw BusinessException.info("已是最后一层，无法下移");
    }
    Teachplan downPlan = sortedPlans.get(index + 1);
    Integer temp = upPlan.getOrderby();
    upPlan.setOrderby(downPlan.getOrderby());
    downPlan.setOrderby(temp);
    teachplanMapper.updateById(upPlan);
    teachplanMapper.updateById(downPlan);
  }

  @Override
  @Transactional
  public void moveUp(Long teachPlanId) {
    Teachplan downPlan =
        teachplanMapper.selectOne(
            Wrappers.lambdaQuery(Teachplan.class).eq(Teachplan::getId, teachPlanId));
    List<Teachplan> sortedSameParentPlans = getSortedSameParentPlans(downPlan);
    int index = sortedSameParentPlans.indexOf(downPlan);
    if (index - 1 < 0) {
      throw BusinessException.info("已是最上一层，无法上移");
    }
    Teachplan upPlan = sortedSameParentPlans.get(index - 1);
    Integer temp = downPlan.getOrderby();
    downPlan.setOrderby(upPlan.getOrderby());
    upPlan.setOrderby(temp);
    teachplanMapper.updateById(upPlan);
    teachplanMapper.updateById(downPlan);
  }

  @Transactional
  @Override
  public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
    Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
    Teachplan teachplan = teachplanMapper.selectById(teachplanId);
    if (teachplan == null) {
      throw BusinessException.info("教学计划不存在");
    }
    Integer grade = teachplan.getGrade();
    if (grade != 2) {
      throw BusinessException.info("只允许第二级教学计划绑定媒资文件");
    }
    Long courseId = teachplan.getCourseId();

    teachplanMediaMapper.delete(
        new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));

    TeachplanMedia teachplanMedia = new TeachplanMedia();
    teachplanMedia.setCourseId(courseId);
    teachplanMedia.setTeachplanId(teachplanId);
    teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
    teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
    teachplanMedia.setCreateDate(LocalDateTime.now());
    teachplanMediaMapper.insert(teachplanMedia);
    return teachplanMedia;
  }

  private List<Teachplan> getSortedSameParentPlans(Teachplan upPlan) {
    LambdaQueryWrapper<Teachplan> wrapper = Wrappers.lambdaQuery(Teachplan.class);
    Long parentId = upPlan.getParentid();
    wrapper.eq(Teachplan::getParentid, parentId);
    wrapper.eq(Teachplan::getCourseId, upPlan.getCourseId());
    List<Teachplan> sameParentIdTeachPlans = teachplanMapper.selectList(wrapper);
    return sameParentIdTeachPlans.stream()
        .sorted(Comparator.comparingInt(Teachplan::getOrderby))
        .collect(Collectors.toList());
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
