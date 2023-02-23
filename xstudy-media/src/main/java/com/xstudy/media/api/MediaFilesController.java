package com.xstudy.media.api;

import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.po.MediaFiles;
import com.xstudy.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class MediaFilesController {

  private final MediaFileService mediaFileService;

  @ApiOperation("媒资列表查询接口")
  @PostMapping
  public PageResult<MediaFiles> list(
      PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
    Long companyId = 1232141425L;
    return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
  }
}
