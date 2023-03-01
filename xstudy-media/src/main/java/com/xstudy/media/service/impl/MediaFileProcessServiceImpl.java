package com.xstudy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xstudy.media.model.po.MediaFiles;
import com.xstudy.media.model.po.MediaProcess;
import com.xstudy.media.model.po.MediaProcessHistory;
import com.xstudy.media.repository.MediaFilesMapper;
import com.xstudy.media.repository.MediaProcessHistoryMapper;
import com.xstudy.media.repository.MediaProcessMapper;
import com.xstudy.media.service.MediaFileProcessService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

  private final MediaProcessMapper mediaProcessMapper;
  private final MediaFilesMapper mediaFilesMapper;

  private final MediaProcessHistoryMapper mediaProcessHistoryMapper;

  @Override
  public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
    return mediaProcessMapper.listShard(shardTotal, shardIndex, count);
  }

  @Transactional
  @Override
  public void saveProcessFinishStatus(
      Long taskId, String status, String fileId, String url, String errorMsg) {
    MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
    if (mediaProcess == null) {
      return;
    }
    LambdaQueryWrapper<MediaProcess> queryWrapperById =
        new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
    // 任务失败
    if (status.equals("3")) {
      MediaProcess failedMediaProcess = new MediaProcess();
      failedMediaProcess.setStatus("3");
      failedMediaProcess.setErrorMsg(errorMsg);
      mediaProcessMapper.update(failedMediaProcess, queryWrapperById);
      return;
    }

    MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
    if (mediaFiles != null) {
      mediaFiles.setUrl(url);
      mediaFilesMapper.updateById(mediaFiles);
    }
    // 处理成功，更新url和状态
    mediaProcess.setUrl(url);
    mediaProcess.setStatus("2");
    mediaProcess.setFinishDate(LocalDateTime.now());
    mediaProcessMapper.updateById(mediaProcess);

    // 添加到历史记录
    MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
    BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
    mediaProcessHistoryMapper.insert(mediaProcessHistory);
    // 删除mediaProcess
    mediaProcessMapper.deleteById(mediaProcess.getId());
  }
}
