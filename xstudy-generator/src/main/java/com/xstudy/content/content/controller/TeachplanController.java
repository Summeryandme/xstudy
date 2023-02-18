package com.xstudy.content.content.controller;

import com.xstudy.content.content.service.TeachplanService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 课程计划 前端控制器
 * </p>
 *
 * @author summery
 */
@Slf4j
@RestController
@RequestMapping("teachplan")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;
}
