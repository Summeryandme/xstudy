package com.xstudy.media.service;

import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.po.MediaFiles;

public interface MediaFileService {

  public PageResult<MediaFiles> queryMediaFiels(
      Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);
}
