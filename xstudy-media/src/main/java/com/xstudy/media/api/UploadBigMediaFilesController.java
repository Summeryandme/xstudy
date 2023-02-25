package com.xstudy.media.api;

import com.xstudy.base.model.RestResponse;
import com.xstudy.media.model.dto.UploadFileParamsDto;
import com.xstudy.media.service.MediaFileService;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("upload")
public class UploadBigMediaFilesController {
  private final MediaFileService mediaFileService;

  @ApiOperation(value = "文件上传前检查文件")
  @GetMapping("/checkfile")
  public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
    return mediaFileService.checkFile(fileMd5);
  }

  @ApiOperation(value = "分块文件上传前的检测")
  @PostMapping("/checkchunk")
  public RestResponse<Boolean> checkChunk(
      @RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") int chunk) {
    return mediaFileService.checkChunk(fileMd5, chunk);
  }

  @ApiOperation(value = "上传分块文件")
  @PostMapping("/uploadchunk")
  public RestResponse<Boolean> uploadChunk(
      @RequestParam("file") MultipartFile file,
      @RequestParam("fileMd5") String fileMd5,
      @RequestParam("chunk") int chunk)
      throws IOException {
    return mediaFileService.uploadChunk(fileMd5, chunk, file.getBytes());
  }

  @ApiOperation(value = "合并文件")
  @PostMapping("/mergechunks")
  public RestResponse<Boolean> mergeChunks(
      @RequestParam("fileMd5") String fileMd5,
      @RequestParam("fileName") String fileName,
      @RequestParam("chunkTotal") int chunkTotal)
      throws Exception {
    UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
    uploadFileParamsDto.setFileType("001002");
    uploadFileParamsDto.setTags("课程视频");
    uploadFileParamsDto.setRemark("");
    uploadFileParamsDto.setFilename(fileName);
    return mediaFileService.mergeChunks(123L, fileMd5, chunkTotal, uploadFileParamsDto);
  }
}
