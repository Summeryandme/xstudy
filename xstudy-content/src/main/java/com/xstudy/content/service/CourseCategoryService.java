package com.xstudy.content.service;

import com.xstudy.content.model.dto.CourseCategoryTreeDto;
import java.util.List;

public interface CourseCategoryService {
  List<CourseCategoryTreeDto> queryTreeNodes();
}
