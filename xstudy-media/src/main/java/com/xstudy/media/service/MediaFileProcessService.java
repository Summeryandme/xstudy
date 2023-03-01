package com.xstudy.media.service;

import com.xstudy.media.model.po.MediaProcess;
import java.util.List;

public interface MediaFileProcessService {

  List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

  void saveProcessFinishStatus(
      Long taskId, String status, String fileId, String url, String errorMsg);
}
