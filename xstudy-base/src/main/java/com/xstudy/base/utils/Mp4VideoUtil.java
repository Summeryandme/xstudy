package com.xstudy.base.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Mp4VideoUtil extends VideoUtil {

  private final String ffmpegPath; // ffmpeg的安装位置
  private final String videoPath;
  private final String mp4Name;
  private final String mp4folderPath;

  public Mp4VideoUtil(String ffmpegPath, String videoPath, String mp4Name, String mp4folderPath) {
    super(ffmpegPath);
    this.ffmpegPath = ffmpegPath;
    this.videoPath = videoPath;
    this.mp4Name = mp4Name;
    this.mp4folderPath = mp4folderPath;
  }
  // 清除已生成的mp4
  private void clearMp4(String mp4Path) throws IOException {
    // 删除原来已经生成的m3u8及ts文件
    Files.deleteIfExists(new File(mp4Path).toPath());
  }
  /**
   * 视频编码，生成mp4文件
   *
   * @return 成功返回success，失败返回控制台日志
   */
  public String generateMp4() throws IOException {
    clearMp4(mp4folderPath);
    /*
    ffmpeg.exe -i  lucene.avi -c:v libx264 -s 1280x720 -pix_fmt yuv420p -b:a 63k -b:v 753k -r 18 .\lucene.mp4
     */
    List<String> commend = new ArrayList<>();
    commend.add(ffmpegPath);
    commend.add("-i");
    commend.add(videoPath);
    commend.add("-c:v");
    commend.add("libx264");
    commend.add("-y"); // 覆盖输出文件
    commend.add("-s");
    commend.add("1280x720");
    commend.add("-pix_fmt");
    commend.add("yuv420p");
    commend.add("-b:a");
    commend.add("63k");
    commend.add("-b:v");
    commend.add("753k");
    commend.add("-r");
    commend.add("18");
    commend.add(mp4folderPath);
    String outstring = null;
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command(commend);
      // 将标准输入流和错误输入流合并，通过标准输入流程读取信息
      builder.redirectErrorStream(true);
      Process p = builder.start();
      outstring = waitFor(p);

    } catch (Exception ex) {

      ex.printStackTrace();
    }
    boolean checkVideoTime = this.check_video_time(videoPath, mp4folderPath);
    if (!checkVideoTime) {
      return outstring;
    } else {
      return "success";
    }
  }
}
