package com.xstudy.content.api;

import com.xstudy.content.model.dto.BindTeachplanMediaDto;
import com.xstudy.content.model.dto.SaveTeachPlanDto;
import com.xstudy.content.model.dto.TeachplanDto;
import com.xstudy.content.service.TeachPlanService;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    return teachPlanService.getTree(courseId);
  }

  @PostMapping
  public void saveTeachPlan(@RequestBody SaveTeachPlanDto saveTeachPlanDto) {
    teachPlanService.save(saveTeachPlanDto);
  }

  @DeleteMapping("{teachplanId}")
  public void deleteTeachPlan(@PathVariable Long teachplanId) {
    teachPlanService.delete(teachplanId);
  }

  @PostMapping("/movedown/{teachplanId}")
  public void moveDownTeachPlan(@PathVariable Long teachplanId) {
    teachPlanService.moveDown(teachplanId);
  }

  @PostMapping("/moveup/{teachplanId}")
  public void moveUpTeachPlan(@PathVariable Long teachplanId) {
    teachPlanService.moveUp(teachplanId);
  }

  @PostMapping("/association/media")
  public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto) {
    teachPlanService.associationMedia(bindTeachplanMediaDto);
  }
}
