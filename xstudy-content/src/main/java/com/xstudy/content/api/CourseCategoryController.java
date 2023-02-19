package com.xstudy.content.api;

import com.xstudy.content.model.dto.CourseCategoryTreeDto;
import com.xstudy.content.service.CourseCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/course-category")
public class CourseCategoryController {

  private final CourseCategoryService courseCategoryService;

  @GetMapping("/tree-nodes")
  public List<CourseCategoryTreeDto> queryTreeNodes() {
    return courseCategoryService.queryTreeNodes();
  }
}
