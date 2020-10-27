/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Lorenzo Delgado
 * Copyright (c) 2016 Serhii Pirohov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package es.lnsd.videotape.core.config;

import es.lnsd.videotape.core.config.utils.LowerCaseConverter;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Path;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "${conf.file}",
    "classpath:video.properties",
    "classpath:presets/ffmpeg-${os.type}.properties"
})
public interface VideotapeConfiguration extends Config {

  /*
   * Common
   */

  @Key("video.folder")
  @DefaultValue("${user.dir}/video")
  File folder();

  @Key("video.enabled")
  @DefaultValue("true")
  Boolean videoEnabled();

  @Key("video.mode")
  @ConverterClass(RecordingMode.Converter.class)
  @DefaultValue("ANNOTATED")
  RecordingMode mode();

  @Key("video.save.mode")
  @ConverterClass(SavingStrategy.Converter.class)
  @DefaultValue("FAILED_ONLY")
  SavingStrategy saveStrategy();

  @Key("video.name")
  String fileName();

  @Key("video.format")
  @ConverterClass(LowerCaseConverter.class)
  @DefaultValue("mp4")
  String fileFormat();

  @Key("video.recorder.type")
  @ConverterClass(RecorderType.Converter.class)
  @DefaultValue("MONTE")
  RecorderType recorderType();

  @Key("video.remote.hub")
  @DefaultValue("http://localhost:4444")
  String remoteUrl();

  @Key("video.remote")
  @DefaultValue("false")
  Boolean isRemote();

  @Key("video.frame.rate")
  @DefaultValue("24")
  int frameRate();

  // Dynamic configuration value
  default Dimension screenSize() {
    return Toolkit.getDefaultToolkit().getScreenSize();
  }


  /*
   * FFMPEG specific
   */

  @Key("video.ffmpeg.binary")
  Path ffmpegBinary();

  @Key("video.ffmpeg.format")
  String ffmpegFormat();

  @Key("video.ffmpeg.display")
  String ffmpegDisplay();

  @Key("video.ffmpeg.pixelFormat")
  @DefaultValue("yuv420p")
  String ffmpegPixelFormat();
}
