package com.xstudy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.media.mapper.MediaFilesMapper;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.po.MediaFiles;
import com.xstudy.media.service.MediaFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

  private final MediaFilesMapper mediaFilesMapper;

  @Override
  public PageResult<MediaFiles> queryMediaFiels(
      Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

    // 构建查询条件对象
    LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

    // 分页对象
    Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
    // 查询数据内容获得结果
    Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
    // 获取数据列表
    List<MediaFiles> list = pageResult.getRecords();
    // 获取数据总数
    long total = pageResult.getTotal();
    // 构建结果集
    return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  }
}
