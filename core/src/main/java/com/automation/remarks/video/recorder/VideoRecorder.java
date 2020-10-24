package com.automation.remarks.video.recorder;

import com.automation.remarks.video.VideoConfiguration;
import java.io.File;
import org.aeonbits.owner.ConfigFactory;

import static com.automation.remarks.video.SystemUtils.getOsType;

/**
 * Created by sepi on 19.07.16.
 */
public abstract class VideoRecorder implements IVideoRecorder {
  private static File lastVideo;

  public static VideoConfiguration conf() {
    ConfigFactory.setProperty("os.type", getOsType());
    return ConfigFactory.create(VideoConfiguration.class);
  }

  public static File getLastRecording() {
    return lastVideo;
  }

  protected void setLastVideo(File video) {
    lastVideo = video;
  }
}
