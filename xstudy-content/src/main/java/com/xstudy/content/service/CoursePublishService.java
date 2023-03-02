package com.xstudy.content.service;

import com.xstudy.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {

  CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
