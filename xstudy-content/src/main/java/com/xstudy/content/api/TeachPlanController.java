package com.xstudy.content.api;

import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.service.TeachPlanService;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/teachplan")
@Validated
public class TeachPlanController {

  private final TeachPlanService teachPlanService;

  @GetMapping("{courseId}/tree-nodes")
  public List<TeachplanDto> getTreeNodes(@PathVariable @NotNull Long courseId) {
    return teachPlanService.findTeachPlanTree(courseId);
  }
}
