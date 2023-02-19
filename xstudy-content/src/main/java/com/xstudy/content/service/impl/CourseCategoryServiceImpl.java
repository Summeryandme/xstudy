package com.xstudy.content.service.impl;

import com.xstudy.content.mapper.CourseCategoryMap;
import com.xstudy.content.model.dto.CourseCategoryTreeDto;
import com.xstudy.content.model.po.CourseCategory;
import com.xstudy.content.repository.CourseCategoryMapper;
import com.xstudy.content.service.CourseCategoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl implements CourseCategoryService {

  private final CourseCategoryMapper courseCategoryMapper;

  private final CourseCategoryMap courseCategoryMap;

  @Override
  public List<CourseCategoryTreeDto> queryTreeNodes() {
    List<CourseCategory> categories = courseCategoryMapper.selectList(null);
    List<CourseCategoryTreeDto> categoryTreeDtoList =
        categories.stream()
            .map(courseCategoryMap::toCourseCategoryTreeDto)
            .collect(Collectors.toList());
    CourseCategoryTreeDto root =
        categoryTreeDtoList.stream()
            .filter(courseCategoryTreeDto -> courseCategoryTreeDto.getParentid().equals("0"))
            .collect(Collectors.toList())
            .get(0);
    root.setChildrenTreeNodes(getChildren(root, categoryTreeDtoList));
    return root.getChildrenTreeNodes();
  }

  private List<CourseCategoryTreeDto> getChildren(
      CourseCategoryTreeDto categoryTreeDto, List<CourseCategoryTreeDto> categoryTreeDtoList) {
    List<CourseCategoryTreeDto> children =
        categoryTreeDtoList.stream()
            .filter(
                courseCategoryTreeDto ->
                    courseCategoryTreeDto.getParentid().equals(categoryTreeDto.getId()))
            .collect(Collectors.toList());
    children.forEach(
        courseCategoryTreeDto ->
            courseCategoryTreeDto.setChildrenTreeNodes(
                getChildren(courseCategoryTreeDto, categoryTreeDtoList)));
    return children.isEmpty() ? null : children;
  }
}
