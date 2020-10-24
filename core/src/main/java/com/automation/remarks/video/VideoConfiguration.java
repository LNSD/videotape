package com.automation.remarks.video;

import com.automation.remarks.video.enums.RecorderType;
import com.automation.remarks.video.enums.RecordingMode;
import com.automation.remarks.video.enums.VideoSaveMode;
import java.awt.*;
import java.io.File;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

/**
 * Created by sergey on 4/13/16.
 */
@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "classpath:video.properties",
    "classpath:ffmpeg-${os.type}.properties"
})
public interface VideoConfiguration extends Config {

  @Key("video.folder")
  @DefaultValue("${user.dir}/video")
  File folder();

  @Key("video.enabled")
  @DefaultValue("true")
  Boolean videoEnabled();

  @Key("video.mode")
  @DefaultValue("ANNOTATED")
  RecordingMode mode();

  @Key("video.remote.hub")
  @DefaultValue("http://localhost:4444")
  String remoteUrl();

  @Key("video.remote")
  @DefaultValue("false")
  Boolean isRemote();

  @Key("video.name")
  String fileName();

  @Key("video.recorder.type")
  @DefaultValue("MONTE")
  RecorderType recorderType();

  @Key("video.save.mode")
  @DefaultValue("FAILED_ONLY")
  VideoSaveMode saveMode();

  @Key("video.frame.rate")
  @DefaultValue("24")
  int frameRate();

  @Key("video.screen.size")
  default Dimension screenSize() {
    return SystemUtils.getSystemScreenDimension();
  }

  @Key("video.ffmpeg.format")
  String ffmpegFormat();

  @Key("video.ffmpeg.display")
  String ffmpegDisplay();

  @Key("video.ffmpeg.pixelFormat")
  @DefaultValue("yuv420p")
  String ffmpegPixelFormat();
}
