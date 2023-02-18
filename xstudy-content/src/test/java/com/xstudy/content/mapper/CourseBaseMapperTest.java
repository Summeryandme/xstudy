package com.xstudy.content.mapper;

import com.xstudy.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourseBaseMapperTest {
  @Autowired
  CourseBaseMapper courseBaseMapper;

  @Test
  void should_work() {
    CourseBase courseBase = courseBaseMapper.selectById(1);
    System.out.println(courseBase);
  }
}