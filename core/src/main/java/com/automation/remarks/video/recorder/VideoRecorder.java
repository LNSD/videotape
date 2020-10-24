package com.automation.remarks.video.recorder;

import com.automation.remarks.video.VideoConfiguration;
import com.automation.remarks.video.enums.OperatingSystem;
import java.io.File;
import lombok.Getter;
import lombok.Setter;
import org.aeonbits.owner.ConfigFactory;


/**
 * Created by sepi on 19.07.16.
 */
public abstract class VideoRecorder implements IVideoRecorder {

  @Getter
  @Setter
  private static File lastVideo;

  public static VideoConfiguration conf() {
    ConfigFactory.setProperty("os.type", OperatingSystem.get());
    return ConfigFactory.create(VideoConfiguration.class);
  }
}
