package com.xstudy.content.service;

import com.xstudy.content.model.dto.TeachplanDto;
import java.util.List;

public interface TeachPlanService {
  List<TeachplanDto> findTeachPlanTree(long courseId);
}
