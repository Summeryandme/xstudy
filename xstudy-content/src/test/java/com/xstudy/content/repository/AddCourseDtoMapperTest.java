package com.xstudy.content.repository;

import com.xstudy.content.model.po.CourseBase;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AddCourseDtoMapperTest {
  @Resource
  CourseBaseMapper courseBaseMapper;

  @Test
  void should_work() {
    CourseBase courseBase = courseBaseMapper.selectById(1);
    System.out.println(courseBase);
  }
}