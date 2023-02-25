package com.xstudy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xstudy.base.exception.BusinessException;
import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.dto.UploadFileParamsDto;
import com.xstudy.media.model.dto.UploadFileResultDto;
import com.xstudy.media.model.po.MediaFiles;
import com.xstudy.media.repository.MediaFilesMapper;
import com.xstudy.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MediaFileServiceImpl implements MediaFileService {

  private final MediaFilesMapper mediaFilesMapper;

  private final MinioClient minioClient;

  @Autowired private MediaFileService proxy;

  @Value("${minio.bucket.files}")
  private String mediaBucket;

  @Override
  public PageResult<MediaFiles> queryMediaFiels(
      Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {
    LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
    Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
    Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
    List<MediaFiles> list = pageResult.getRecords();
    long total = pageResult.getTotal();
    return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  }

  @Override
  public UploadFileResultDto uploadFile(
      Long companyId,
      UploadFileParamsDto uploadFileParamsDto,
      byte[] bytes,
      String folder,
      String objectName) {
    String fileId = DigestUtils.md5Hex(bytes);
    String filename = uploadFileParamsDto.getFilename();
    if (StringUtils.isEmpty(objectName)) {
      objectName = fileId + filename.substring(filename.lastIndexOf("."));
    }
    if (StringUtils.isEmpty(folder)) {
      folder = getFileFolder();
    } else if (!folder.contains("/")) {
      folder = folder + "/";
    }
    objectName = folder + objectName;
    MediaFiles mediaFiles;
    addMediaFilesToMinIO(bytes, mediaBucket, objectName);
    mediaFiles = addMediaFilesToDb(companyId, fileId, uploadFileParamsDto, mediaBucket, objectName);
    UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
    BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
    return uploadFileResultDto;
  }

  public void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    String extension = null;
    if (objectName.contains(".")) {
      extension = objectName.substring(objectName.lastIndexOf("."));
    }
    String contentType = getMimeTypeByExtension(extension);
    try {
      PutObjectArgs putObjectArgs =
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              // -1表示文件分片按5M(不小于5M,不大于5T),分片数量最大10000，
              .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
              .contentType(contentType)
              .build();

      minioClient.putObject(putObjectArgs);
    } catch (Exception e) {
      log.error("update file error", e.getCause());
      throw BusinessException.info("上传文件到文件系统出错");
    }
  }

  private String getMimeTypeByExtension(String extension) {
    String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    if (StringUtils.isNotEmpty(extension)) {
      ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
      if (extensionMatch != null) {
        contentType = extensionMatch.getMimeType();
      }
    }
    return contentType;
  }

  public MediaFiles addMediaFilesToDb(
      Long companyId,
      String fileMd5,
      UploadFileParamsDto uploadFileParamsDto,
      String bucket,
      String objectName) {
    MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
    if (mediaFiles == null) {
      mediaFiles = new MediaFiles();
      BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
      mediaFiles.setId(fileMd5);
      mediaFiles.setCompanyId(companyId);
      mediaFiles.setUrl("/" + bucket + "/" + objectName);
      mediaFiles.setBucket(bucket);
      mediaFiles.setCreateDate(LocalDateTime.now());
      mediaFiles.setAuditStatus("002003");
      mediaFiles.setStatus("1");
      // 保存文件信息到文件表
      int insert = mediaFilesMapper.insert(mediaFiles);
      if (insert < 0) {
        throw BusinessException.info("保存文件信息失败");
      }
    }
    return mediaFiles;
  }

  private String getFileFolder() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = sdf.format(new Date());
    String[] dateStringArray = dateString.split("-");
    return dateStringArray[0] + "/" + dateStringArray[1] + "/" + dateStringArray[2] + "/";
  }
}
