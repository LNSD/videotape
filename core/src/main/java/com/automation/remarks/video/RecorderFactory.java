package com.automation.remarks.video;

import com.automation.remarks.video.enums.RecorderType;
import com.automation.remarks.video.recorder.IVideoRecorder;
import com.automation.remarks.video.recorder.ffmpeg.LinuxFFmpegRecorder;
import com.automation.remarks.video.recorder.ffmpeg.MacFFmpegRecorder;
import com.automation.remarks.video.recorder.ffmpeg.WindowsFFmpegRecorder;
import com.automation.remarks.video.recorder.monte.MonteRecorder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.SystemUtils;

/**
 * Created by sepi on 19.07.16.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecorderFactory {

  public static IVideoRecorder getRecorder(RecorderType recorderType) {

    if (recorderType == RecorderType.FFMPEG) {
      if (SystemUtils.IS_OS_WINDOWS) {
        return new WindowsFFmpegRecorder();
      }
      if (SystemUtils.IS_OS_MAC) {
        return new MacFFmpegRecorder();
      }
      if (SystemUtils.IS_OS_LINUX) {
        return new LinuxFFmpegRecorder();
      }

      String os = System.getProperty("os.name", "unknown");
      throw new NotImplementedException(String.format("OS '%s' not supported", os));
    }

    return new MonteRecorder();
  }
}
