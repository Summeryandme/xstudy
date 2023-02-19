package com.xstudy.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstudy.system.model.po.Dictionary;
import java.util.List;

public interface DictionaryService extends IService<Dictionary> {

  List<Dictionary> queryAll();

  Dictionary getByCode(String code);
}
