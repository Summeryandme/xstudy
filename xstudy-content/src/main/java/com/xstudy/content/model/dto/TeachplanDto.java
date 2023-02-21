package com.xstudy.content.model.dto;

import com.xstudy.content.model.po.Teachplan;
import com.xstudy.content.model.po.TeachplanMedia;
import java.util.List;
import lombok.Data;

@Data
public class TeachplanDto extends Teachplan {

  private TeachplanMedia teachplanMedia;

  private List<TeachplanDto> teachPlanTreeNodes;

}