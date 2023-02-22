package com.xstudy.content.service;

import com.xstudy.content.model.dto.SaveTeachPlanDto;
import com.xstudy.content.model.dto.TeachplanDto;
import java.util.List;

public interface TeachPlanService {
  List<TeachplanDto> getTree(long courseId);

  void save(SaveTeachPlanDto teachplanDto);

  void delete(Long teachPlanId);

  void moveDown(Long teachPlanId);
}
