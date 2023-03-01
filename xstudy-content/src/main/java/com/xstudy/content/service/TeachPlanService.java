package com.xstudy.content.service;

import com.xstudy.content.model.dto.BindTeachplanMediaDto;
import com.xstudy.content.model.dto.SaveTeachPlanDto;
import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.model.po.TeachplanMedia;
import java.util.List;

public interface TeachPlanService {
  List<TeachplanDto> getTree(long courseId);

  void save(SaveTeachPlanDto teachplanDto);

  void delete(Long teachPlanId);

  void moveDown(Long teachPlanId);

  void moveUp(Long teachPlanId);

  TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
