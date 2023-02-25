package com.xstudy.media.api;

import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.media.map.MediaFileMap;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.dto.UploadFileParamsDto;
import com.xstudy.media.model.dto.UploadFileResultDto;
import com.xstudy.media.model.po.MediaFiles;
import com.xstudy.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class MediaFilesController {

  private final MediaFileService mediaFileService;

  private final MediaFileMap mediaFileMap;

  @ApiOperation("媒资列表查询接口")
  @PostMapping("files")
  public PageResult<MediaFiles> list(
      PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
    Long companyId = 1232141425L;
    return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
  }

  @ApiOperation("上传文件")
  @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public UploadFileResultDto upload(
      @RequestPart("filedata") MultipartFile multipartFile,
      @RequestParam(value = "folder", required = false) String folder,
      @RequestParam(value = "objectName", required = false) String objectName)
      throws IOException {
    UploadFileParamsDto uploadFileParamsDto =
        mediaFileMap.toUploadFileParamsDto(multipartFile, folder, objectName);

    Long companyId = 123L;
    return mediaFileService.uploadFile(
        companyId, uploadFileParamsDto, multipartFile.getBytes(), folder, objectName);
  }
}
