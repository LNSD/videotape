package com.automation.remarks.video;

import com.automation.remarks.video.annotations.Video;
import com.automation.remarks.video.enums.RecordingMode;
import com.automation.remarks.video.enums.VideoSaveMode;
import com.automation.remarks.video.recorder.VideoRecorder;
import java.io.File;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by sergey on 4/21/16.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecordingUtils {

  public static String doVideoProcessing(boolean successfulTest, File video) {
    String filePath = formatVideoFilePath(video);
    if (!successfulTest || isSaveAllModeEnable()) {
      log.info("Video recording: {}", filePath);
      return filePath;
    } else if (video != null && video.isFile()) {
      if (!video.delete()) {
        log.info("Video didn't deleted");
        return "Video didn't deleted";
      }
      log.info("No video on success test");
    }
    return "";
  }

  private static boolean isSaveAllModeEnable() {
    return VideoRecorder.conf().saveMode().equals(VideoSaveMode.ALL);
  }

  public static boolean videoEnabled(Video video) {
    return VideoRecorder.conf().videoEnabled()
        && (isRecordingAllModeEnable() || video != null);
  }

  private static boolean isRecordingAllModeEnable() {
    return VideoRecorder.conf().mode().equals(RecordingMode.ALL);
  }

  public static String getVideoFileName(Video annotation, String methodName) {
    if (useNameFromVideoAnnotation(annotation) && VideoRecorder.conf().fileName() != null) {
      return VideoRecorder.conf().fileName();
    } else if (annotation == null) {
      return methodName;
    }
    String name = annotation.name();
    return name.length() > 1 ? name : methodName;
  }

  private static boolean useNameFromVideoAnnotation(Video annotation) {
    return annotation == null || annotation.name().isEmpty();
  }

  // TODO Review this
  private static String formatVideoFilePath(File video) {
    if (video == null) {
      return "";
    }
    String jenkinsReportsUrl = getJenkinsReportsUrl();
    if (!isBlank(jenkinsReportsUrl)) {
      return jenkinsReportsUrl + video.getName();
    }
    return video.getAbsolutePath();
  }

  // TODO Review this
  private static String getJenkinsReportsUrl() {
    String jenkinsUrl = System.getProperty("jenkinsUrl");
    if (!isBlank(jenkinsUrl)) {
      return jenkinsUrl;
    } else {
      log.info("No jenkinsUrl variable found.");
      return "";
    }
  }
}
