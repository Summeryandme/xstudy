package com.xstudy.media.service;

import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.dto.UploadFileParamsDto;
import com.xstudy.media.model.dto.UploadFileResultDto;
import com.xstudy.media.model.po.MediaFiles;

public interface MediaFileService {

  PageResult<MediaFiles> queryMediaFiels(
      Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

  UploadFileResultDto uploadFile(
      Long companyId,
      UploadFileParamsDto uploadFileParamsDto,
      byte[] bytes,
      String folder,
      String objectName);

  MediaFiles addMediaFilesToDb(
      Long companyId,
      String fileMd5,
      UploadFileParamsDto uploadFileParamsDto,
      String bucket,
      String objectName);
}
