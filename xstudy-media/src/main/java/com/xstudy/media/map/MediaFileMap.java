package com.xstudy.media.map;

import com.xstudy.media.model.dto.UploadFileParamsDto;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface MediaFileMap {

  @Mapping(source = "multipartFile.size", target = "fileSize")
  @Mapping(source = "multipartFile", target = "fileType", qualifiedByName = "toContentType")
  @Mapping(source = "multipartFile.originalFilename", target = "filename")
  @Mapping(source = "multipartFile.contentType", target = "contentType")
  UploadFileParamsDto toUploadFileParamsDto(
      MultipartFile multipartFile, String folder, String objectName);

  @Named("toContentType")
  default String toContentType(MultipartFile multipartFile) {
    String multipartFileContentType = multipartFile.getContentType();
    if (Objects.requireNonNull(multipartFileContentType).contains("image")) {
      return "001001";
    } else {
      return "001003";
    }
  }
}
