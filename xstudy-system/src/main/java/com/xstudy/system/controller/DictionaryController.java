package com.xstudy.system.controller;

import com.xstudy.system.model.po.Dictionary;
import com.xstudy.system.service.DictionaryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DictionaryController {

  private final DictionaryService dictionaryService;

  @GetMapping("/dictionary/all")
  public List<Dictionary> queryAll() {
    return dictionaryService.queryAll();
  }

  @GetMapping("/dictionary/code/{code}")
  public Dictionary getByCode(@PathVariable String code) {
    return dictionaryService.getByCode(code);
  }
}
