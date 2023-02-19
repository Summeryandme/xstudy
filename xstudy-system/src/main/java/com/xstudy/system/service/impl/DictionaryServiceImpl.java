package com.xstudy.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstudy.system.model.po.Dictionary;
import com.xstudy.system.mapper.DictionaryMapper;
import com.xstudy.system.service.DictionaryService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary>
    implements DictionaryService {

  @Override
  public List<Dictionary> queryAll() {
    return this.list();
  }

  @Override
  public Dictionary getByCode(String code) {

    LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Dictionary::getCode, code);

    return this.getOne(queryWrapper);
  }
}
