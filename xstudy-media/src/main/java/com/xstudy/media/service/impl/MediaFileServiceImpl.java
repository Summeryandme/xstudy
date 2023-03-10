package com.xstudy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xstudy.base.exception.BusinessException;
import com.xstudy.base.model.PageParams;
import com.xstudy.base.model.PageResult;
import com.xstudy.base.model.RestResponse;
import com.xstudy.media.model.dto.QueryMediaParamsDto;
import com.xstudy.media.model.dto.UploadFileParamsDto;
import com.xstudy.media.model.dto.UploadFileResultDto;
import com.xstudy.media.model.po.MediaFiles;
import com.xstudy.media.model.po.MediaProcess;
import com.xstudy.media.repository.MediaFilesMapper;
import com.xstudy.media.repository.MediaProcessMapper;
import com.xstudy.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

  private final MediaProcessMapper mediaProcessMapper;

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

  @Override
  public RestResponse<Boolean> checkFile(String fileMd5) {
    MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
    if (mediaFiles == null) {
      return RestResponse.success(false);
    }
    try {
      GetObjectResponse object =
          minioClient.getObject(
              GetObjectArgs.builder()
                  .bucket(mediaFiles.getBucket())
                  .object(mediaFiles.getFilePath())
                  .build());
      return RestResponse.success(object != null);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw BusinessException.info("get ????????????");
    }
  }

  @Override
  public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
    String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
    String chunkFilePath = chunkFileFolderPath + chunkIndex;

    try {
      return RestResponse.success(
          minioClient.getObject(
                  GetObjectArgs.builder().bucket(mediaBucket).object(chunkFilePath).build())
              != null);
    } catch (Exception e) {
      log.error("??????????????????", e);
      throw BusinessException.info("??????????????????");
    }
  }

  @Override
  public RestResponse<Boolean> uploadChunk(String fileMd5, int chunk, byte[] bytes) {
    String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
    String chunkFilePath = chunkFileFolderPath + chunk;
    try {
      addMediaFilesToMinIO(bytes, mediaBucket, chunkFilePath);
      return RestResponse.success(true);
    } catch (Exception e) {
      e.printStackTrace();
      log.debug("??????????????????:{},??????:{}", chunkFilePath, e.getMessage());
    }
    return RestResponse.validFail(false, "??????????????????");
  }

  @Override
  public RestResponse<Boolean> mergeChunks(
      Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto)
      throws IOException {
    String fileName = uploadFileParamsDto.getFilename();
    File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);
    String extName = fileName.substring(fileName.lastIndexOf("."));
    File mergeFile = File.createTempFile(fileMd5, extName);
    byte[] buffer = new byte[1024];
    try (RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFile, "rw")) {
      for (File chunkFile : chunkFiles) {
        try (FileInputStream fileInputStream = new FileInputStream(chunkFile)) {
          int len;
          while ((len = fileInputStream.read(buffer)) != -1) {
            randomAccessFile.write(buffer, 0, len);
          }
        }
      }
    }
    log.info("?????????????????? {}", mergeFile.getAbsolutePath());
    uploadFileParamsDto.setFileSize(mergeFile.length());
    try (InputStream mergeFileInputStream = Files.newInputStream(mergeFile.toPath())) {
      String newFileMd5 = DigestUtils.md5Hex(mergeFileInputStream);
      if (!fileMd5.equalsIgnoreCase(newFileMd5)) {
        throw BusinessException.info("????????????????????????");
      }
      log.info("????????????????????????{}", mergeFile.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
      throw BusinessException.info("????????????????????????");
    }

    String mergeFilePath = getFilePathByMd5(fileMd5, extName);
    try {
      // ???????????????minIO
      addMediaFilesToMinIO(mergeFile.getAbsolutePath(), mediaBucket, mergeFilePath);
      log.info("??????????????????MinIO??????{}", mergeFile.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
      throw BusinessException.info("?????????????????????????????????");
    }
    MediaFiles mediaFiles =
        addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, mediaBucket, mergeFilePath);
    if (mediaFiles == null) {
      throw BusinessException.info("????????????????????????");
    }
    for (File chunkFile : chunkFiles) {
      Files.deleteIfExists(chunkFile.toPath());
    }
    Files.deleteIfExists(mergeFile.toPath());

    return RestResponse.success();
  }

  @Override
  public MediaFiles getFileById(String mediaId) {
    return mediaFilesMapper.selectById(mediaId);
  }

  private String getFilePathByMd5(String fileMd5, String fileExt) {
    return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
  }

  private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
    String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
    File[] files = new File[chunkTotal];
    for (int i = 0; i < chunkTotal; i++) {
      String chunkFilePath = chunkFileFolderPath + i;
      File chunkFile;
      try {
        chunkFile = File.createTempFile("chunk" + i, null);
      } catch (IOException e) {
        e.printStackTrace();
        throw BusinessException.info("???????????????????????????????????????");
      }
      downloadFileFromMinIO(chunkFile, mediaBucket, chunkFilePath);
      files[i] = chunkFile;
    }
    return files;
  }

  @Override
  public void downloadFileFromMinIO(File file, String bucket, String objectName) {
    try (InputStream fileInputStream =
            minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectName).build());
        OutputStream fileOutputStream = Files.newOutputStream(file.toPath())) {
      IOUtils.copy(fileInputStream, fileOutputStream);
    } catch (Exception e) {
      log.error("downloadFileFromMinIO error", e);
    }
  }

  private String getChunkFileFolderPath(String fileMd5) {
    return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
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
              // -1?????????????????????5M(?????????5M,?????????5T),??????????????????10000???
              .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
              .contentType(contentType)
              .build();

      minioClient.putObject(putObjectArgs);
    } catch (Exception e) {
      log.error("update file error", e.getCause());
      throw BusinessException.info("?????????????????????????????????");
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
    String extension = null;
    if (objectName.contains(".")) {
      extension = objectName.substring(objectName.lastIndexOf("."));
    }
    String mineType = getMimeTypeByExtension(extension);

    MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
    if (mediaFiles == null) {
      mediaFiles = new MediaFiles();
      BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
      mediaFiles.setId(fileMd5);
      mediaFiles.setCompanyId(companyId);
      if (mineType.contains("image") || mineType.contains("mp4")) {
        mediaFiles.setUrl("/" + bucket + "/" + objectName);
      }
      mediaFiles.setFilePath(objectName);
      mediaFiles.setBucket(bucket);
      mediaFiles.setCreateDate(LocalDateTime.now());
      mediaFiles.setAuditStatus("002003");
      mediaFiles.setStatus("1");
      mediaFilesMapper.insert(mediaFiles);

      if (mineType.equals("video/x-msvideo")) {
        MediaProcess mediaProcess = new MediaProcess();
        BeanUtils.copyProperties(mediaFiles, mediaProcess);
        mediaProcess.setStatus("1"); // ?????????
        mediaProcessMapper.insert(mediaProcess);
      }
    }
    return mediaFiles;
  }

  @Override
  public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {
    // ?????????
    String extension = null;
    if (objectName.contains(".")) {
      extension = objectName.substring(objectName.lastIndexOf("."));
    }
    // ????????????????????????????????????
    String contentType = getMimeTypeByExtension(extension);
    try {
      minioClient.uploadObject(
          UploadObjectArgs.builder()
              .bucket(bucket)
              .object(objectName)
              .filename(filePath)
              .contentType(contentType)
              .build());
    } catch (Exception e) {
      e.printStackTrace();
      throw BusinessException.info("?????????????????????????????????");
    }
  }

  private String getFileFolder() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = sdf.format(new Date());
    String[] dateStringArray = dateString.split("-");
    return dateStringArray[0] + "/" + dateStringArray[1] + "/" + dateStringArray[2] + "/";
  }
}
