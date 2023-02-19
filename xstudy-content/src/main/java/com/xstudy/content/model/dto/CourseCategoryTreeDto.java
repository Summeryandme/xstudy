package com.xstudy.content.model.dto;

import com.xstudy.content.model.po.CourseCategory;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CourseCategoryTreeDto extends CourseCategory {
  private List<CourseCategoryTreeDto> childrenTreeNodes;
}
