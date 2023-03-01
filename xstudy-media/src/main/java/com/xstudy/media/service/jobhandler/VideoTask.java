package com.xstudy.media.service.jobhandler;

import com.xstudy.base.utils.Mp4VideoUtil;
import com.xstudy.media.model.po.MediaProcess;
import com.xstudy.media.service.MediaFileProcessService;
import com.xstudy.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoTask {
  private final MediaFileService mediaFileService;
  private final MediaFileProcessService mediaFileProcessService;
  private final ThreadPoolTaskExecutor videoProcessExecutor;

  @Value("${videoprocess.ffmpegpath}")
  private String ffmpegPath;

  @XxlJob("videoJobHandler")
  public void videoJobHandler() throws InterruptedException {
    int shardIndex = XxlJobHelper.getShardIndex();
    int shardTotal = XxlJobHelper.getShardTotal();
    List<MediaProcess> mediaProcessList =
        mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 2);
    int size = mediaProcessList.size();
    CountDownLatch countDownLatch = new CountDownLatch(size);
    mediaProcessList.forEach(
        mediaProcess -> videoProcessExecutor.submit(
            () -> {
              String bucket = mediaProcess.getBucket();
              String filePath = mediaProcess.getFilePath();
              String fileId = mediaProcess.getFileId();
              if ("2".equals(mediaProcess.getStatus())) {
                return;
              }
              File originalFile;
              File mp4File;
              try {
                originalFile = File.createTempFile("original", null);
                mp4File = File.createTempFile("mp4File", ".mp4");
              } catch (IOException e) {
                log.error("处理视频前创建临时文件失败");
                countDownLatch.countDown();
                return;
              }
              mediaFileService.downloadFileFromMinIO(
                  originalFile, mediaProcess.getBucket(), filePath);
              Mp4VideoUtil videoUtil =
                  new Mp4VideoUtil(
                      ffmpegPath,
                      originalFile.getAbsolutePath(),
                      mp4File.getName(),
                      mp4File.getAbsolutePath());
              String result;
              try {
                result = videoUtil.generateMp4();
              } catch (IOException e) {
                log.error("处理视频文件:{},出错:{}", filePath, e.getMessage());
                countDownLatch.countDown();
                return;
              }
              if (!result.equals("success")) {
                log.error("处理视频失败,视频地址:{},错误信息:{}", bucket + filePath, result);
                mediaFileProcessService.saveProcessFinishStatus(
                    mediaProcess.getId(), "3", fileId, null, result);
                countDownLatch.countDown();
                return;
              }
              String objectName = null;
              try {
                objectName = getFilePath(fileId);
                mediaFileService.addMediaFilesToMinIO(
                    mp4File.getAbsolutePath(), bucket, objectName);
              } catch (Exception e) {
                log.error("上传视频失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
                countDownLatch.countDown();
                return;
              }
              try {
                // 访问url
                String url = "/" + bucket + "/" + objectName;
                mediaFileProcessService.saveProcessFinishStatus(
                    mediaProcess.getId(), "2", fileId, url, null);
              } catch (Exception e) {
                log.error("视频信息入库失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
              }
              countDownLatch.countDown();
            }));
    boolean await = countDownLatch.await(30, TimeUnit.MINUTES);
    if (!await) {
      log.error("处理视频超时");
    }

  }

  private String getFilePath(String fileMd5) {
    return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + ".mp4";
  }
}
